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

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.common.AuditableEntity;
import com.openkoda.model.common.AuditableEntityOrganizationRelated;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * This factory creates change description for audit logs.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Service
public class AuditChangeFactory implements LoggingComponentWithRequestId {

    /**
     * This method creates change description
     *
     * @param auditedObject   object that is subject of audit
     * @param aos             state of the object that is subject of audit
     * @param entityClassName a {@link java.lang.String} object.
     * @return Change description prepared for audit log.
     * @see AuditableEntityOrganizationRelated
     * @see PropertyChangeListener
     */
    public String createChange(AuditableEntity auditedObject, AuditedObjectState aos, String entityClassName) {
        debug("[createChange] {} {} {}", auditedObject, aos, entityClassName);
        StringBuilder change = new StringBuilder();
        change.append(entityClassName);
        switch (aos.getOperation()) {
            case ADD:
                return getAddChangeDescription(aos, entityClassName, change);
            case EDIT:
                return getEditChangeDescription(auditedObject, aos, entityClassName, change);
            case DELETE:
                return getDeleteChangeDescription(auditedObject, entityClassName, change);
            default:
                return "";
        }
    }

    /**
     * Creates change log for new object created.
     */
    private String getAddChangeDescription(AuditedObjectState aos, String entityClass, StringBuilder change) {
        debug("[getAddChangeDescription] entityClass: {}", entityClass);
        change.append(" created with:<br/>");
        writeProperties(aos, change);
        writeContent(aos, change);
        return change.toString();
    }

    /**
     * Creates change log for object update.
     */
    private String getEditChangeDescription(AuditableEntity p, AuditedObjectState aos, String entityClass, StringBuilder change) {
        debug("[getEditChangeDescription] entityClass: {}", entityClass);
        change.append(" ").append(p.toAuditString()).append("<br/>");
        writeProperties(aos, change);
        writeContent(aos, change);
        return change.toString();
    }

    /**
     * Creates change log for object deleted.
     */
    private String getDeleteChangeDescription(AuditableEntity p, String entityClass, StringBuilder change) {
        debug("[getDeleteChangeDescription] entityClass: {}", entityClass);
        change.append("Deleted ").append(entityClass).append(" ").append(p.toAuditString());
        return change.toString();
    }

    /**
     * Writes object properties changelog into given string builder
     */
    private void writeProperties(AuditedObjectState aos, StringBuilder change) {
        debug("[writeProperties] {} {}", aos, change);
        if(aos.getProperties().isEmpty()){
            return;
        }
        aos.getProperties().forEach((k, v) ->
                change.append("<b>").append(getDefaultFieldLabel(k))
                        .append("</b> ").append(v).append("<br/>")
        );
    }

    /**
     * Writes object content changelog into given string builder
     */
    private void writeContent(AuditedObjectState aos, StringBuilder change) {
        debug("[writeContent] {} {}", aos, change);
        if (aos.getContent() == null) {
            return;
        }
        change.append("New values for properties:</br>");
        change.append("<b>Content</b> </br>");
    }

    /**
     * Creates a label for the given field by convention. <br/>
     * The routine is to split fieldName by camel case and make words upper case.
     * @return generated default label for a given field.
     */
    private String getDefaultFieldLabel(String fieldName) {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(fieldName)), ' ');
    }
}
