package com.openkoda.controller.admin;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.model.Privilege;
import org.springframework.data.domain.Pageable;

public class AbstractAuditController extends AbstractController {

    protected PageModelMap findAll(Pageable auditPageable, String search){
        return Flow.init()
                .thenSet( auditPage, a -> repositories.secure.audit.search(search, Privilege.canReadSupportData, auditPageable))
                .execute();
    }
}
