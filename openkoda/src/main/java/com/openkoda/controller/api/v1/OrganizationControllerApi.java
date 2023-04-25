package com.openkoda.controller.api.v1;

import com.openkoda.controller.api.CRUDApiController;
import com.openkoda.model.Organization;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/organization/{organizationId}/organization","/api/v1/organization"})
public class OrganizationControllerApi extends CRUDApiController<Organization> {
    public OrganizationControllerApi(){
        super("organization");
    }
}
