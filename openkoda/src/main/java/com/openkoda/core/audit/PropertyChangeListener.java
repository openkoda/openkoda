/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.audit;

import com.openkoda.controller.common.SessionData;
import com.openkoda.core.helper.ApplicationContextProvider;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.service.SessionService;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.core.tracker.RequestIdHolder;
import com.openkoda.model.common.Audit;
import com.openkoda.model.common.AuditableEntity;
import com.openkoda.model.common.AuditableEntityOrganizationRelated;
import jakarta.inject.Inject;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

/**
 * Prepares Audit logs and keeps the audit log label for an entity.
 * Also provide a few helper methods for extracting data useful in audit (eg ip, spoof, organization id)
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class PropertyChangeListener implements LoggingComponentWithRequestId {

    @Inject
    SessionService sessionService;

    protected final String entityClass;
    protected final String entityClassLabel;

    public PropertyChangeListener(String className, String entityClassLabel) {
        this.entityClass = className;
        this.entityClassLabel = entityClassLabel;
    }

    /**
     * Prepares the audit log for a given entity
     * @return single element {@link Audit} array.
     */
    public Audit[] prepareAuditLogs(Object entity, Optional<OrganizationUser> user, AuditedObjectState aos, Optional<Collection<?>> userRoleIds) {
        debug("prepareAuditLogs {} {} {} {}", entity, user, aos, userRoleIds);
        Audit audit = createAudit(entity, user, aos, userRoleIds);
        return new Audit[]{audit};
    }

    protected Audit createAudit(Object entity, Optional<OrganizationUser> user, AuditedObjectState aos, Optional<Collection<?>> userRoleIds) {
        debug("[createAudit] {} {} {} {}", entity, user, aos, userRoleIds);
        AuditableEntity auditPrintableEntity = (AuditableEntity) entity;
        return prepareAudit(auditPrintableEntity, aos, user.isPresent() ? user.get() : null, userRoleIds.orElse(null), new Date());
    }

    protected Audit prepareAudit(AuditableEntity p, AuditedObjectState aos, OrganizationUser user, Collection<?> userRoleIds, Date date) {
        debug("[prepareAudit] {} {} {} {} {}", p, aos, user, userRoleIds, date);
        Audit audit = new Audit();
        audit.setOperation(aos.getOperation());
        String changeDescription = getAuditChangeFactory().createChange(p, aos, entityClassLabel);
        audit.setEntityName(entityClass);
        audit.setSeverity(Audit.Severity.INFO);
        audit.setUserRoleIds(userRoleIds);
        audit.setEntityId(getEntityId(p));
        audit.setOrganizationId(getOrganizationId(p));
        audit.setIpAddress(getIpService().getCurrentUserIpAddress());
        audit.setRequestId(RequestIdHolder.getId());
        if (aos.getContent() != null) {
            audit.setContent(aos.getContent());
        }
        if (user != null && user.getUser() != null) {
            audit.setUserId(user.getUser().getId());
            if (user.isSpoofed()) {
                changeDescription = addSpoofInfo(changeDescription);
            }
        }
        audit.setChange(changeDescription);
        return audit;
    }


    /**
     * Extracts from session user spoof info and adds in to audit change description.
     * @return change changeDescription with added spoof info.
     */
    private String addSpoofInfo(String changeDescription) {
        debug("[addSpoofInfo]");
        Long uId = (Long) SessionService.getInstance().getSessionAttribute(SessionData.SPOOFING_USER);
        changeDescription = "<b><i>Spoofed by user with id: " + uId + "</b></i></br>" + changeDescription;
        return changeDescription;
    }


    /**
     * Checks whether {@link AuditableEntity} is OrganizationRelated
     * @return organization id or null if the entity is not organization related
     */
    protected Long getOrganizationId(AuditableEntity entity) {
        debug("[getOrganizationId]");
        if (entity instanceof AuditableEntityOrganizationRelated) {
            AuditableEntityOrganizationRelated auditableEntityOrganizationRelated = (AuditableEntityOrganizationRelated) entity;
            return auditableEntityOrganizationRelated.getOrganizationId();
        }
        debug("[getOrganizationId] no AuditableEntity {} found", entity);
        return null;
    }

    protected Long getEntityId(AuditableEntity p) {
        return p.getId();
    }

    private AuditChangeFactory getAuditChangeFactory() {
        return getContext().getBean(AuditChangeFactory.class);
    }

    private IpService getIpService() {
        return getContext().getBean(IpService.class);
    }

    private ApplicationContext getContext() {
        return ApplicationContextProvider.getContext();
    }

}
