package timelog;

import com.openkoda.App;
import com.openkoda.core.customisation.CustomisationService;
import com.openkoda.core.form.FrontendMappingDefinition;
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
    TicketRepository ticketRepository;

    @Inject
    AssignmentRepository assignmentRepository;

    @Inject
    TimelogRepository timelogRepository;

    @Inject
    CustomisationService customisationService;

    @PostConstruct
    void init() {
        FrontendMappingDefinition ticket = FrontendMappingDefinition.createFrontendMappingDefinition(
                "ticket", canAccessGlobalSettings, canAccessGlobalSettings,
                a -> a
                        .datalist("organizations", f -> f.getDictionaryRepository().dictionary(Organization.class))
                        .dropdown("organizationId", "organizations").additionalPrivileges(readOrgData, canAccessGlobalSettings)
                        .text("name")
        );

        FrontendMappingDefinition assignment = FrontendMappingDefinition.createFrontendMappingDefinition(
                "assignment", canAccessGlobalSettings, canAccessGlobalSettings,
                a -> a
                        .datalist("organizations", f -> f.getDictionaryRepository().dictionary(Organization.class))
                        .dropdown("organizationId", "organizations").additionalPrivileges(readOrgData, canAccessGlobalSettings)
                        .datalist("tickets", f -> f.getDictionaryRepository().dictionary(Ticket.class))
                        .datalist("users", f -> f.getDictionaryRepository().dictionary(User.class))
                        .dropdown("ticketId", "tickets")
                        .dropdown("userId", "users")
                        .hidden("description")
                        .checkbox("billable"));

        FrontendMappingDefinition timelog = FrontendMappingDefinition.createFrontendMappingDefinition(
                "timelog", readOrgData, readOrgData,
                a -> a.datalist("assignments", f -> f.getDictionaryRepository().dictionary(Assignment.class))
                        .dropdown("assignmentId", "assignments")
                        .date("startedOn")
                        .number("duration"));


        customisationService.registerOnApplicationStartListener(
            c -> {
                c.registerCrudController(ticket, ticketRepository).setGenericTableFields("name");
                c.registerCrudController(assignment, assignmentRepository).setGenericTableFields("description");
                c.registerFrontendMapping(timelog, timelogRepository);
            }
        );

    }

}
