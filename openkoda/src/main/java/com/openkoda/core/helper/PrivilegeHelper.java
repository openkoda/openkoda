/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.helper;

import com.google.gson.internal.LinkedTreeMap;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.security.UserProvider;
import com.openkoda.model.DynamicPrivilege;
import com.openkoda.model.Privilege;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.PrivilegeGroup;
import com.openkoda.service.user.BasicPrivilegeService;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.openkoda.core.helper.NameHelper.getClasses;
import static com.openkoda.model.Privilege.*;
import static java.util.stream.Collectors.toList;

/**
 *
 * <p>PrivilegeHelper class</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Component("auth")
@Scope("singleton")
public class PrivilegeHelper implements HasSecurityRules, AttributeConverter<PrivilegeBase, String> {

    @Value("${authentication.loginAndPassword:true}")
    public boolean loginAndPasswordAuthentication;
    @Value("${login.sign-up.link:false}")
    public boolean signUpLink;
    @Value("${application.classes.privileges-enum:}")
    private String[] privilegesEnumClasses;

    private static List<PrivilegeBase> allEnumPrivileges;
    private static List<PrivilegeBase> allNonHiddenEnumPrivileges;
    private static final Map<String, PrivilegeBase> nameToEnum = new LinkedTreeMap<>();
    private static JSONArray enumJsonArray;
    private static JSONArray enumJsonArrayWithLabel;
    
    private static final Set<PrivilegeBase> adminPrivileges = new HashSet<>(Arrays.asList(Privilege.values()));
    private static final Set<PrivilegeBase> userPrivileges = new HashSet<>();
    private static final Set<PrivilegeBase> orgAdminPrivileges = new HashSet<>();
    private static final Set<PrivilegeBase> orgUserPrivileges = new HashSet<>();
    
    private static AtomicReference<PrivilegeHelper> instance;
    
    /**
     * Refer to any repository-like method via service to make use of Cacheable mechanism
     */
    @Inject private BasicPrivilegeService privilegeService;

    private PrivilegeHelper() {
        PrivilegeHelper.instance = new AtomicReference<>(this);
    }
    
    public static final PrivilegeHelper getInstance() {
        if(instance == null) {
            instance =  new AtomicReference<>(new PrivilegeHelper());
        }
        
        return instance.get();
    }

    public Long getCurrentUserId() {
        return UserProvider.getFromContext().map( a-> a.getUser() ).map( a -> a.getId() ).orElse(null );
    }

    public Long getCurrentOrganizationId() {
        return UserProvider.getFromContext().map( a-> a.getDefaultOrganizationId() ).orElse(null );
    }

    public static Enum[] fromJoinedStringToArray(String joinedPrivileges) {
        return Arrays.stream(joinedPrivileges.split(",")).map(PrivilegeHelper::valueOfString).toArray(Enum[]::new);
    }

    public static Set<PrivilegeBase> fromJoinedStringToSet(String joinedPrivileges) {
        return Arrays.stream(joinedPrivileges.split(",")).map(PrivilegeHelper::valueOfString).collect(Collectors.toUnmodifiableSet());
    }

    public static Set<String> fromJoinedStringToStringSet(String joinedPrivileges) {
        return streamSplitAndRemoveParenthesis(joinedPrivileges).collect(Collectors.toUnmodifiableSet());
    }

    //TODO: this method is handy not only for privileges, move to some more general helper
    public static Stream<String> streamSplitAndRemoveParenthesis(String joinedPrivileges) {
        return Arrays.stream(splitAndRemoveParenthesis(joinedPrivileges));
    }

    public static Set<String> fromPrivilegesToStringSet(PrivilegeBase... p) {
        return Arrays.stream(p).map(a -> a.name()).collect(Collectors.toUnmodifiableSet());
    }

    public static Set<PrivilegeBase> fromJoinedStringInParenthesisToPrivilegeEnumSet(String joinedValuesInParenthesis) {
        Stream<String> k = streamSplitAndRemoveParenthesis(joinedValuesInParenthesis);
        return k.map(PrivilegeHelper::valueOfString).collect(Collectors.toUnmodifiableSet());
    }


    public static Set<Long> fromJoinedStringInParenthesisToLongSet(String joinedValuesInParenthesis) {
        return streamSplitAndRemoveParenthesis(joinedValuesInParenthesis).map(Long::valueOf).collect(Collectors.toUnmodifiableSet());
    }

    public static PrivilegeBase valueOfString(String s) {
        if(StringUtils.isBlank(s)) {
            return null;
        }
        
        PrivilegeBase privelege = nameToEnum.get(s);
        if(privelege == null && getInstance().privilegeService != null) {
            privelege = getInstance().privilegeService.findByName(s);
        }
        
        return privelege;
    }


    /**
     * Separates words in parentheses and removes them
     * @param joinedPrivileges
     * @return an array of strings without parentheses
     */
    private static String[] splitAndRemoveParenthesis(String joinedPrivileges) {
        return StringUtils.isEmpty(joinedPrivileges) ? new String[0]
                : joinedPrivileges.substring(1, joinedPrivileges.length() - 1).split("\\),\\(");
    }

    /**
     * @param e
     * @return String in parentheses
     */
    public static String inParenthesis(PrivilegeBase e) {
        return "(" + e.name() + ")";
    }


    /**
     * @param privilegesSet
     * @return joined string in parentheses
     */
    public static String toJoinedStringInParenthesis(Set<PrivilegeBase> privilegesSet) {
        return StringUtils.join(privilegesSet.stream().map(PrivilegeHelper::inParenthesis).collect(toList()), ',');
    }

    /**
     * @param privileges
     * @return joined string in parentheses
     */
    public static String toJoinedStringInParenthesis(PrivilegeBase ... privileges) {
        return StringUtils.join(Stream.of(privileges).map(PrivilegeHelper::inParenthesis).collect(toList()), ',');
    }

    public static List<PrivilegeBase> allEnumsToList() {
        List<DynamicPrivilege> dymamicPrivileges = 
                getInstance().privilegeService != null 
                ? getInstance().privilegeService.findAll() 
                : Collections.emptyList();
        if(dymamicPrivileges.isEmpty()) {
            return allEnumPrivileges;
        }
        
        List<PrivilegeBase> privileges = new ArrayList<>(allEnumPrivileges);
        privileges.addAll(dymamicPrivileges);
        return privileges;
    }

    // TODO : can be removed?
    public static Map<String, PrivilegeBase> getNameToEnum() {
        return nameToEnum;
    }

    public static List<PrivilegeBase> getAllNonHiddenEnumPrivileges() {
        if (allNonHiddenEnumPrivileges == null) {
            allNonHiddenEnumPrivileges = nameToEnum.values().stream().filter( p -> !p.isHidden()).sorted( (p1, p2) -> p1.getId().compareTo(p2.getId()) ).toList();
        }
        
        return allNonHiddenEnumPrivileges;
    }
    
    /**
     * @return map of all enums as privilege base list
     */
    public static List<PrivilegeBase> allEnumsAsPrivilegeBaseList() {
        List<DynamicPrivilege> dynamicPrivileges = getInstance().privilegeService != null 
                ? getInstance().privilegeService.findAll() 
                : Collections.emptyList();
        if(dynamicPrivileges.isEmpty()) {
            // just return already pre-populated list
            return getAllNonHiddenEnumPrivileges();
        }
        
        List<PrivilegeBase> result = new ArrayList<>(getAllNonHiddenEnumPrivileges());
        result.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
        result.addAll(0, dynamicPrivileges);
        return result;
    }

    public static PrivilegeBase[] allEnumsAsPrivilegeBase() {
        return allEnumsAsPrivilegeBaseList().toArray(PrivilegeBase[]::new);
    }

    // TODO : is that used at all? can be removed?
    /**
     * @return map of all enums as privilege base linked map
     */
    public static Map<PrivilegeBase, String> allEnumsAsPrivilegeBaseLinkedMap() {
        TreeMap<PrivilegeBase, String> result = new TreeMap<>(Comparator.comparing(PrivilegeBase::name));
        for (Map.Entry<String, PrivilegeBase> e: nameToEnum.entrySet()) {
            PrivilegeBase pb = e.getValue();
            if (pb.isHidden()) { continue; }
            result.put(pb, pb.getLabel());
        }
        
        if(getInstance().privilegeService != null) {
            getInstance().privilegeService.findAll().forEach( pb -> result.put(pb, pb.name()));
        }
        
        return result;
    }

    public static String allEnumsAsPrivilegeBaseJsonString(boolean concatLabel) throws JSONException {
        return getInstance().allEnumsAsPrivilegeBaseJsonStringInstance(concatLabel);
    }
    
    public static List<? extends PrivilegeBase> getAllEnumPrivileges() {
        if (allEnumPrivileges == null) {
            allEnumPrivileges = new ArrayList<>(nameToEnum.values());
        }
        
        return allEnumPrivileges;
    }
    
    public String allEnumsAsPrivilegeBaseJsonStringInstance(boolean concatLabel) throws JSONException {
        if(!concatLabel && enumJsonArray == null) {
            enumJsonArray = privilegeListToJson(new JSONArray(), getAllEnumPrivileges(), concatLabel);
        } else if(concatLabel && enumJsonArrayWithLabel == null) {
            enumJsonArrayWithLabel = privilegeListToJson(new JSONArray(), getAllEnumPrivileges(), concatLabel);
        }
        
        List<DynamicPrivilege> dynamicPrivileges = privilegeService.findAll();
        if (dynamicPrivileges.isEmpty()) {
            if(concatLabel) {
                return enumJsonArrayWithLabel.toString();
            }
            
            return enumJsonArray.toString();
        }

        JSONArray results = new JSONArray();
        privilegeListToJson(results, getAllEnumPrivileges(), concatLabel);
        privilegeListToJson(results, dynamicPrivileges, concatLabel);
        return results.toString();
    }
    
    private JSONArray privilegeListToJson(JSONArray results, List<? extends PrivilegeBase> privileges, boolean concatLabel) throws JSONException {
        JSONObject result;
        for (PrivilegeBase pb : privileges) {
            result = new JSONObject();
            result.put("k", pb.name());
            if(!concatLabel) {
                result.put("c", pb.getCategory());
                if(pb.getGroup() != null) {
                    result.put("g", pb.getGroup().getLabel());
                }
                result.put("v", pb.getLabel());
            } else {
                result.put("v", (pb.getGroup() != null ? pb.getGroup().getLabel() : pb.getCategory()) + ": " + pb.getLabel());
            }
            result.put("hidden", String.valueOf(pb.isHidden()));
            results.put(result);
        }
        
        return results;
    }

    /**
     * Static method which add all enum values to nameToEnum map
     *
     * @param enumClasses
     */
    public static void registerEnumClasses(Class<Enum>[] enumClasses) {
        for (Class<Enum> ec : enumClasses) {
            List<Enum> enums = Arrays.stream(ec.getEnumConstants()).sorted(Comparator.comparing(o -> ((PrivilegeBase) o).getLabel())).collect(toList());
            for (Enum e : enums) {
                nameToEnum.put(e.name(), (PrivilegeBase)e);
            }
        }
    }

    public static Set<PrivilegeBase> getAdminPrivilegeSet() {
        return adminPrivileges.stream().map( p -> (PrivilegeBase)p).collect(Collectors.toSet());
    }

    public static Set<PrivilegeBase> getOrgAdminPrivilegeSet() {
        return orgAdminPrivileges.stream().map( p -> (PrivilegeBase)p).collect(Collectors.toSet());
    }

    public static Set<PrivilegeBase> getUserPrivilegeSet() {
        return userPrivileges.stream().map( p -> (PrivilegeBase)p).collect(Collectors.toSet());
    }

    public static Set<PrivilegeBase> getOrgUserPrivilegeSet() {
        return orgUserPrivileges.stream().map( p -> (PrivilegeBase)p).collect(Collectors.toSet());
    }

    public static Set<String> getAdminPrivilegeStrings() {
        return fromPrivilegesToStringSet(allEnumsAsPrivilegeBase());
    }

    public static Privilege[] getAdminPrivileges() {
        return Privilege.values();
    }
    
    public static List<PrivilegeGroup> allPrivilegeGroups() {
        return Arrays.asList(PrivilegeGroup.values());
    }
    
    public static List<String> allCategories() {
        return Stream.of(Privilege.values()).map(p -> p.getCategory()).sorted().toList();
    }

    /**
     * a post constructor that initializes the attributes of the class
     */
    @PostConstruct
    void init() {
        registerEnumClasses((Class<Enum>[]) getClasses(privilegesEnumClasses));
        orgAdminPrivileges.addAll(adminPrivileges);
        orgAdminPrivileges.removeAll(Arrays.asList(canAccessGlobalSettings,canImpersonate,canSeeUserEmail,canResetPassword,canChangeEntityOrganization));
        orgUserPrivileges.addAll(Arrays.asList(readUserData, readOrgData));
        userPrivileges.addAll(Arrays.asList(isUser));
    }

    @Override
    public String convertToDatabaseColumn(PrivilegeBase attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public PrivilegeBase convertToEntityAttribute(String dbData) {
        return PrivilegeHelper.valueOfString(dbData);
    }
}
