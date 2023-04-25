package timelog.repository;

import com.openkoda.core.repository.common.SearchableFunctionalRepositoryWithLongId;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import org.springframework.stereotype.Repository;
import timelog.model.Ticket;

@Repository
@SearchableRepositoryMetadata(
    entityClass = Ticket.class,
    entityKey = "ticket",
    descriptionFormula = "(name)"
)
public interface TicketRepository extends SearchableFunctionalRepositoryWithLongId<Ticket> {
}
