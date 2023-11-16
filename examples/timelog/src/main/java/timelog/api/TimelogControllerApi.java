package timelog.api;

import com.openkoda.controller.api.CRUDApiController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import timelog.model.Timelog;

import static com.openkoda.controller.common.URLConstants._API_V2;
import static com.openkoda.controller.common.URLConstants._API_V2_ORGANIZATION_ORGANIZATIONID;
import static timelog.TimelogApp.TIMELOG;
import static timelog.TimelogApp._TIMELOG;

@RestController
@RequestMapping({_API_V2_ORGANIZATION_ORGANIZATIONID + _TIMELOG, _API_V2 + _TIMELOG})
public class TimelogControllerApi extends CRUDApiController<Timelog> {

    public TimelogControllerApi() {
        super(TIMELOG);
    }
}
