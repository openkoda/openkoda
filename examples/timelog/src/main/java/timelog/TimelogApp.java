package timelog;

import com.openkoda.App;
import com.openkoda.core.customisation.CustomisationService;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.helper.UrlHelper;
import com.openkoda.model.Organization;
import com.openkoda.model.User;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import timelog.model.Assignment;
import timelog.model.Ticket;
import timelog.model.Timelog;
import timelog.repository.AssignmentRepository;
import timelog.repository.TicketRepository;
import timelog.repository.TimelogRepository;

import static com.openkoda.model.Privilege.canAccessGlobalSettings;
import static com.openkoda.model.Privilege.readOrgData;

@SpringBootApplication(scanBasePackages = {"com.openkoda", "timelog"})
@ComponentScan({"com.openkoda", "timelog"})
@EnableJpaRepositories({"com.openkoda", "timelog.repository"})
@EntityScan({"com.openkoda", "timelog.model"})
public class TimelogApp extends App {

    public static void main(String[] args) {
        startApp(TimelogApp.class, args);
    }

    @Inject
    TimelogRepository timelogRepository;

    @Inject
    CustomisationService customisationService;

    @PostConstruct
    void init() {


        customisationService.registerOnApplicationStartListener(
            c -> {
            }
        );

    }

}
