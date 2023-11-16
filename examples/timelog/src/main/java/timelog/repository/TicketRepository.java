package timelog.repository;

import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.repository.SecureRepository;
import org.springframework.stereotype.Repository;
import timelog.model.Ticket;

import static timelog.TimelogApp.TICKET;

@Repository
@SearchableRepositoryMetadata(
    entityClass = Ticket.class,
    entityKey = TICKET,
    descriptionFormula = "(name)"
)
public interface TicketRepository extends SecureRepository<Ticket> {
}
