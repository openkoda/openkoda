package timelog.api;

import com.openkoda.controller.api.CRUDApiController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import timelog.model.CreativeWorkStatement;

import static com.openkoda.controller.common.URLConstants._API_V2;
import static com.openkoda.controller.common.URLConstants._API_V2_ORGANIZATION_ORGANIZATIONID;
import static timelog.TimelogApp.*;

@RestController
@RequestMapping({ _API_V2_ORGANIZATION_ORGANIZATIONID + _TIMELOG + _CREATIVEWORKSTATEMENT, _API_V2 + _TIMELOG + _CREATIVEWORKSTATEMENT})
public class CreativeWorkStatementControllerApi extends CRUDApiController<CreativeWorkStatement> {
    public CreativeWorkStatementControllerApi() {
        super(CREATIVEWORKSTATEMENT);
    }
}
