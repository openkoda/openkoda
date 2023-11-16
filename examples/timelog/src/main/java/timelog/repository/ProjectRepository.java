package timelog.repository;

import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.repository.SecureRepository;
import org.springframework.stereotype.Repository;
import timelog.model.Project;

@Repository
@SearchableRepositoryMetadata(
        entityClass = Project.class,
        entityKey = "project",
        descriptionFormula = "(name)"
)
public interface ProjectRepository extends SecureRepository<Project> {
}
