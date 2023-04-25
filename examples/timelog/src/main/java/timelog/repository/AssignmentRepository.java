package timelog.repository;

import com.openkoda.core.repository.common.SearchableFunctionalRepositoryWithLongId;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import org.springframework.stereotype.Repository;
import timelog.model.Assignment;

import static timelog.model.Assignment.descriptionFormula;

@Repository
@SearchableRepositoryMetadata(
    entityClass = Assignment.class,
    entityKey = "assignment",
    descriptionFormula = descriptionFormula
)
public interface AssignmentRepository extends SearchableFunctionalRepositoryWithLongId<Assignment> {
}
