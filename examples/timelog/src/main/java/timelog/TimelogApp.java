package timelog;

import com.openkoda.App;
import com.openkoda.core.customisation.CustomisationService;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import timelog.repository.TimelogRepository;

@SpringBootApplication(scanBasePackages = {"com.openkoda", "timelog"})
@ComponentScan({"com.openkoda", "timelog"})
@EnableJpaRepositories({"com.openkoda", "timelog.repository"})
@EntityScan({"com.openkoda", "timelog.model"})
public class TimelogApp extends App {

    public static final String ASSIGNMENT = "assignment";
    public static final String TICKET = "ticket";
    public static final String CREATIVEWORKSTATEMENT = "creativeWorkStatement";
    public static final String _CREATIVEWORKSTATEMENT = "/" + CREATIVEWORKSTATEMENT;
    public static final String TIMELOG = "timelog";
    public static final String _TIMELOG = "/" + TIMELOG;
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
