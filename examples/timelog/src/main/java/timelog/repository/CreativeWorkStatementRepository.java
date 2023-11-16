package timelog.repository;

import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.repository.SecureRepository;
import org.springframework.stereotype.Repository;
import timelog.model.CreativeWorkStatement;

import static com.openkoda.model.common.ModelConstants.DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA;
import static timelog.TimelogApp.CREATIVEWORKSTATEMENT;

@Repository
@SearchableRepositoryMetadata(
        entityClass = CreativeWorkStatement.class,
        entityKey = CREATIVEWORKSTATEMENT,
        descriptionFormula = "(''||full_name||' '||month||' '||year)",
        searchIndexFormula = DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA
)
public interface CreativeWorkStatementRepository extends SecureRepository<CreativeWorkStatement> {

}
