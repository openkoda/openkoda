package timelog.repository;

import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.repository.SecureRepository;
import org.springframework.stereotype.Repository;
import timelog.model.Account;

@Repository
@SearchableRepositoryMetadata(
        entityClass = Account.class,
        entityKey = "account",
        descriptionFormula = "(name)"
)
public interface AccountRepository extends SecureRepository<Account> {
}
