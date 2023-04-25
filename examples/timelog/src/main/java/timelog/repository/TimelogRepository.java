package timelog.repository;

import com.openkoda.core.repository.common.SearchableFunctionalRepositoryWithLongId;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import org.springframework.stereotype.Repository;
import timelog.model.Timelog;

@Repository
@SearchableRepositoryMetadata(
    entityClass = Timelog.class,
    entityKey = "timelog"
)
public interface TimelogRepository extends SearchableFunctionalRepositoryWithLongId<Timelog> {
}
