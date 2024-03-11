package com.openkoda.service.dynamicentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.core.form.FieldType;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.form.FrontendMappingFieldDefinition;
import com.openkoda.core.service.form.FormService;
import com.openkoda.model.common.OpenkodaEntity;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.model.component.Form;
import com.openkoda.model.file.File;
import com.openkoda.repository.SearchableRepositories;
import com.openkoda.repository.SecureRepository;
import jakarta.persistence.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.openkoda.core.helper.NameHelper.*;
import static com.openkoda.model.common.ModelConstants.REQUIRED_READ_PRIVILEGE;
import static com.openkoda.model.common.ModelConstants.REQUIRED_WRITE_PRIVILEGE;
import static com.openkoda.core.helper.NameHelper.toColumnName;
import static com.openkoda.core.helper.NameHelper.toEntityName;
import static java.lang.Character.isUpperCase;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static net.bytebuddy.description.modifier.Visibility.PROTECTED;
import static net.bytebuddy.description.modifier.Visibility.PUBLIC;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION;
import static net.bytebuddy.implementation.attribute.AnnotationValueFilter.Default.SKIP_DEFAULTS;
import static org.apache.commons.lang.StringUtils.uncapitalize;


@Service
public class DynamicEntityRegistrationService{

    public static final String PACKAGE = "com.openkoda.dynamicentity.generated.";
    public static Map<String, Tuple2<String, Class<? extends SecureRepository<? extends OpenkodaEntity>>>> dynamicRepositoryClasses = new HashMap<>();

    @PersistenceContext
    EntityManager em;

    public void registerDynamicRepositories(boolean proceed){
        if(!proceed){
            return;
        }
        for(Map.Entry<String, Tuple2<String, Class<? extends SecureRepository<? extends OpenkodaEntity>>>> entry : dynamicRepositoryClasses.entrySet()) {
            String tableName = entry.getKey();
            Class<? extends SecureRepository<? extends OpenkodaEntity>> repositoryClass = entry.getValue().getT2();

            SearchableRepositories.registerSearchableRepository(tableName, new JpaRepositoryFactory(em).getRepository(repositoryClass));
        }
    }
    public static int generateDynamicEntityDescriptor(Form form, Long timeMilis){
        return generateDynamicEntityDescriptors(List.of(form), timeMilis);
    }
    /**
     * generate dynamicEntityDescriptors for forms
     */
    public static int generateDynamicEntityDescriptors(List<Form> forms, Long timeMilis){
        return generateDynamicEntityDescriptors(forms, forms.stream().collect(toMap(Form::getName, FormService::getFrontendMappingDefinition)), timeMilis);
    }

    public static int generateDynamicEntityDescriptors(List<Form> forms, Map<String, FrontendMappingDefinition> frontendMappingDefinitions, Long timeMilis) {
        int generatedEntities = 0;
//      combine all fields for same table in multiple forms
        Map<String, List<Form>> formsByTableName = forms.stream().collect(groupingBy(Form::getTableName));
        for(Map.Entry<String, List<Form>> formsAssignedToTable : formsByTableName.entrySet()) {
            Map<String, FrontendMappingFieldDefinition> fields = new HashMap<>();
            for (Form form : formsAssignedToTable.getValue()) {
                for(FrontendMappingFieldDefinition field : frontendMappingDefinitions.get(form.getName()).getDbTypeFields()) {
                    if(!fields.containsKey(field.getName())) {
                        fields.put(field.getName(), field);
                    }
                }
            }
            DynamicEntityDescriptorFactory.create(formsAssignedToTable.getKey(), fields.values(), timeMilis);
            generatedEntities++;
        }
        return generatedEntities;
    }
    public static Tuple2<Class<? extends OpenkodaEntity>, Class<? extends SecureRepository<? extends OpenkodaEntity>>> createAndLoadDynamicClasses(DynamicEntityDescriptor ded, ClassLoader cl) {

        try {
             Tuple3<Class<OpenkodaEntity>, String, String> dynamicEntity = createAndLoadDynamicEntity(ded.getSufixedEntityName(), ded.getTableName(), ded.getFields(), cl);
             Class<? extends SecureRepository<? extends OpenkodaEntity>> dynamicRepository = createAndLoadDynamicRepository(
                     dynamicEntity.getT1(), //generated entity class
                     ded.getEntityKey(),
                     dynamicEntity.getT2(), //description formula
                     dynamicEntity.getT3(), //search index formula
                     ded.getSufixedRepositoryName(),
                     cl);
             dynamicRepositoryClasses.put(ded.getTableName(), Tuples.of(ded.getSufixedEntityName().toLowerCase(), dynamicRepository));
             ded.setLoaded(true);
             return Tuples.of(dynamicEntity.getT1(), dynamicRepository);
        }catch (IOException | URISyntaxException e) {
            ded.setLoaded(false);
            e.printStackTrace();
        }
        return null;
    }

    private static <O extends OpenkodaEntity> Tuple3<Class<O>, String, String> createAndLoadDynamicEntity(String name, String tableName, Collection<FrontendMappingFieldDefinition> fields, ClassLoader cl) throws IOException, URISyntaxException {
        AnnotationDescription entity = AnnotationDescription.Builder.ofType(Entity.class)
                .build();
        AnnotationDescription formula = AnnotationDescription.Builder.ofType(Formula.class)
                .define("value", "(null)")
                .build();
        AnnotationDescription tableAnnotation = AnnotationDescription.Builder.ofType(Table.class)
                .define("name", tableName)
                .build();
        AnnotationDescription.Builder columnAnnotation = AnnotationDescription.Builder.ofType(Column.class)
                .define("updatable", true)
                .define("insertable", true);
//        files annotations
        AnnotationDescription foreignKeyAnnotation = AnnotationDescription.Builder.ofType(ForeignKey.class)
                .define("value", ConstraintMode.NO_CONSTRAINT).build();
        AnnotationDescription.Builder joinColumnAnnotation = AnnotationDescription.Builder.ofType(JoinColumn.class);
        TypeDescription joinColumnAnnotationType = AnnotationDescription.Builder.ofType(JoinColumn.class).build().getAnnotationType();
        AnnotationDescription jsonIgnoreAnnotation = AnnotationDescription.Builder.ofType(JsonIgnore.class).build();
        AnnotationDescription manyToManyAnnotation = AnnotationDescription.Builder.ofType(ManyToMany.class)
                .define("fetch", FetchType.LAZY).build();
        AnnotationDescription joinTableAnnotation = AnnotationDescription.Builder.ofType(JoinTable.class)
                .define("name", "file_reference")
                .define("foreignKey", foreignKeyAnnotation)
                .defineAnnotationArray("inverseJoinColumns", joinColumnAnnotationType, joinColumnAnnotation
                        .define("name", "file_id")
                        .build())
                .defineAnnotationArray("joinColumns", joinColumnAnnotationType, joinColumnAnnotation
                        .define("name", "organization_related_entity_id")
                        .define("insertable", false)
                        .define("updatable", false)
                        .build())
                .build();

        AnnotationDescription elementCollectionAnnotation = AnnotationDescription.Builder.ofType(ElementCollection.class)
                .define("fetch", FetchType.LAZY)
                .define("targetClass", Long.class).build();
        AnnotationDescription collectionTableAnnotation = AnnotationDescription.Builder.ofType(CollectionTable.class)
                .define("name", "file_reference")
                .defineAnnotationArray("joinColumns", joinColumnAnnotationType, joinColumnAnnotation
                        .define("name", "organization_related_entity_id")
                        .build())
                .define("foreignKey", foreignKeyAnnotation).build();
        AnnotationDescription orderColumnAnnotation = AnnotationDescription.Builder.ofType(OrderColumn.class)
                .define("name", "sequence").build();

        DynamicType.Unloaded<OpenkodaEntity> entityType = null;
        StringBuilder descriptionFormula = new StringBuilder();
        StringBuilder searchIndexFormula = new StringBuilder();

        try {
            DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<OpenkodaEntity>  dynamicType;

                dynamicType = new ByteBuddy()
                        .with(SKIP_DEFAULTS)
                        .subclass(OpenkodaEntity.class)
                        .name(PACKAGE + name)
                        .annotateType(entity)
                        .annotateType(tableAnnotation)
                        .defineConstructor(PUBLIC)
                        .intercept(MethodCall
                                .invoke(OpenkodaEntity.class.getDeclaredConstructor(Long.class))
                                .with((Object) null));

            for(FrontendMappingFieldDefinition field : fields) {
                Type fieldJavaType = getFieldJavaType(field);
                String dbColumnName = toColumnName(field.getName());

                if (field.getType().equals(FieldType.files)) {
                    dynamicType = dynamicType.defineField("files", listOfType(File.class), PUBLIC)
                            .annotateField(manyToManyAnnotation)
                            .annotateField(joinTableAnnotation)
                            .annotateField(jsonIgnoreAnnotation)
                            .annotateField(orderColumnAnnotation)
                            .defineMethod("getFiles" , listOfType(File.class), PUBLIC).intercept(FieldAccessor.ofField("files"))
                            .defineMethod("setFiles", void.class, PUBLIC).withParameter(listOfType(File.class)).intercept(FieldAccessor.ofField("files"));
                    dynamicType = dynamicType.defineField(field.getName(), listOfType(Long.class), PUBLIC)
                            .annotateField(columnAnnotation.define("name", "file_id").build())
                            .annotateField(elementCollectionAnnotation)
                            .annotateField(collectionTableAnnotation)
                            .annotateField(orderColumnAnnotation)
                            .defineMethod("getFilesId", listOfType(Long.class), PUBLIC).intercept(FieldAccessor.ofField("filesId"))
                            .defineMethod("setFilesId", void.class, PUBLIC).withParameter(listOfType(Long.class)).intercept(FieldAccessor.ofField("filesId"));
                } else {
                    if (field.getType().getDbType().equals(FieldType.text.getDbType())) {
                        descriptionFormula.append("||' '||").append(String.format("COALESCE(%s,'')", dbColumnName));
                    }
                    if (field.getType().getDbType().getColumnType().equals(FieldType.text.getDbType().getColumnType())) {
                        searchIndexFormula.append("||' '||").append(String.format("'%s:'||COALESCE(%s,'')", field.getName(), dbColumnName));
                    } else if (field.getType().equals(FieldType.many_to_one)) {
                        searchIndexFormula.append("||' '||").append(String.format("'%s:'||COALESCE(cast (%s as varchar),'')", field.getName(), dbColumnName));
                    }
                    dynamicType = dynamicType.defineField(field.getName(), fieldJavaType, PUBLIC)
                            .annotateField(columnAnnotation.define("name", dbColumnName).build())
                            .defineMethod(getGetterName(field.getName()), fieldJavaType, PUBLIC).intercept(FieldAccessor.ofField(field.getName()))
                            .defineMethod(getSetterName(field.getName()), void.class, PUBLIC).withParameter(fieldJavaType).intercept(FieldAccessor.ofField(field.getName()));
                }

            }
             entityType = dynamicType
                     .defineField(REQUIRED_READ_PRIVILEGE, String.class, PROTECTED)
                     .annotateField(formula)
                     .defineField(REQUIRED_WRITE_PRIVILEGE, String.class, PROTECTED)
                     .annotateField(formula)
                     .defineMethod("getRequiredReadPrivilege", String.class, PUBLIC)
                     .intercept(FieldAccessor.ofBeanProperty())
                     .defineMethod("getRequiredWritePrivilege", String.class, PUBLIC)
                     .intercept(FieldAccessor.ofBeanProperty())
                     .make();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Class<? extends OpenkodaEntity> newEntity = entityType
                .load(cl, INJECTION)
                .getLoaded();

        return Tuples.of((Class < O >) newEntity, String.format("(''||id%s)", descriptionFormula), String.format("(''||id%s)", searchIndexFormula));
    }
    private static TypeDescription.Generic listOfType(Class c){
        return TypeDescription.Generic.Builder.parameterizedType(List.class, c).build();
    }
    private static <T extends OpenkodaEntity> Class<SecureRepository<T>> createAndLoadDynamicRepository(Class<T> entity,
                                                                                                        String entityKey,
                                                                                                        String entityDescriptionFormula,
                                                                                                        String searchIndexFormula,
                                                                                                        String repositoryName,
                                                                                                        ClassLoader cl) throws IOException, URISyntaxException {
        AnnotationDescription repositoryAnnotation = AnnotationDescription.Builder.ofType(Repository.class)
                .build();
        AnnotationDescription searchableRepositoryAnnotation = AnnotationDescription.Builder.ofType(SearchableRepositoryMetadata.class)
                .define("entityClass", entity)
                .define("entityKey", entityKey)
//                .define("descriptionFormula", entityDescriptionFormula)
                .define("searchIndexFormula", searchIndexFormula)
                .build();

        TypeDescription.Generic secureRepositoryType = TypeDescription.Generic.Builder
                .parameterizedType(SecureRepository.class, entity)
                .build();

        DynamicType.Unloaded<?> repositoryType = new ByteBuddy()
                .with(SKIP_DEFAULTS)
                .makeInterface()
                .implement(secureRepositoryType)
                .annotateType(repositoryAnnotation, searchableRepositoryAnnotation)
                .name(PACKAGE + repositoryName)
                .make();

        Class<?> repo = repositoryType
                .load(cl, INJECTION)
                .getLoaded();

        return (Class<SecureRepository<T>>) repo;
    }

    private static Type getFieldJavaType(FrontendMappingFieldDefinition field) {
        if (field.type.getDbType().getColumnType().equals("varchar")) {
            return String.class;
        }
        return switch (field.type.getDbType()) {
            case BIGINT -> Long.class;
            case NUMERIC -> BigDecimal.class;
            case BOOLEAN -> Boolean.class;
            case DATE -> LocalDate.class;
            case TIMESTAMP_W_TZ -> LocalDateTime.class;
            case TIME_W_TZ -> LocalTime.class;
            default -> null;
        };
    }

    private static String getGetterName(String fieldName){
        return "get" + capitalize(fieldName);
    }

    private static String getSetterName(String fieldName){
        return "set" + capitalize(fieldName);
    }

    private static String capitalize(String fieldName){
        if(fieldName.length() > 1 && isUpperCase(fieldName.charAt(1))){
            return fieldName;
        }
        return StringUtils.capitalize(fieldName);

    }
}