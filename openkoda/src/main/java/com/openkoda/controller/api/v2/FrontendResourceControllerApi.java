package com.openkoda.controller.api.v2;

import com.openkoda.controller.api.CRUDApiController;
import com.openkoda.model.FrontendResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping({"/api/v2/organization/{organizationId}/frontendresource", "/api/v2/frontendresource"})
public class FrontendResourceControllerApi extends CRUDApiController<FrontendResource> {

    public FrontendResourceControllerApi() {
        super("frontendresource");
    }
}

