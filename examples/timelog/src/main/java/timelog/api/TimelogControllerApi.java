package timelog.api;

import com.openkoda.controller.api.CRUDApiController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import timelog.model.Timelog;

@RestController
@RequestMapping({"/api/v2/organization/{organizationId}/timelog", "/api/v2/timelog"})
public class TimelogControllerApi extends CRUDApiController<Timelog> {

    public TimelogControllerApi() {super("timelog");}
}
