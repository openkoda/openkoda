package timelog.repository;

import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.repository.SecureRepository;
import org.springframework.stereotype.Repository;
import timelog.model.Assignment;

import static timelog.TimelogApp.ASSIGNMENT;
import static timelog.model.Assignment.descriptionFormula;

@Repository
@SearchableRepositoryMetadata(
    entityClass = Assignment.class,
    entityKey = ASSIGNMENT,
    descriptionFormula = descriptionFormula
)
public interface AssignmentRepository extends SecureRepository<Assignment> {
}
