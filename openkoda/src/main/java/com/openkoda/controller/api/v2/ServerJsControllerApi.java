package com.openkoda.controller.api.v2;

import com.openkoda.controller.api.CRUDApiController;
import com.openkoda.model.ServerJs;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping({"/api/v2/organization/{organizationId}/serverJs","/api/v2/serverJs"})
public class ServerJsControllerApi extends CRUDApiController<ServerJs> {

    public ServerJsControllerApi() {
        super("serverJs");
    }
}