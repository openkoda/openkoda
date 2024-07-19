package com.openkoda.core.audit;

import com.openkoda.core.helper.DatesHelper;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.common.Audit;
import com.openkoda.model.common.AuditableEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.type.Type;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

public abstract class PersistanceInterceptor implements LoggingComponentWithRequestId {

    protected static final FastDateFormat auditDateFormat = FastDateFormat.getInstance("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Method invoked on entity save. It prepares the diff of properties that should be stored in audit
     * and stores this information in auditMap.
     * @param auditMap map to store audit information for all entities modified in transaction
     * @param entity entity being saved
     * @param id id of the entity
     * @param entityState values of entity properties
     * @param propertyNames names of entity properties
     * @param types unused
     * @return
     */
    public abstract boolean onSave(Map<Object, AuditedObjectState> auditMap, Object entity, Object id, Object[] entityState,
            String[] propertyNames, Type[] types);

    /**
     * Method invoked on entity delete. It prepares the diff of properties that should be stored in audit
     * and stores this information in auditMap.
     * @param auditMap map to store audit information for all entities modified in transaction
     * @param entity entity being saved
     * @param id id of the entity
     * @param entityState values of entity properties - unused
     * @param propertyNames names of entity properties - unused
     * @param types unused
     * @return
     */
    public abstract void onDelete(Map<Object, AuditedObjectState> auditMap, Object entity, Object id, Object[] entityState,
            String[] propertyNames, Type[] types);

    /**
     * Called when an object is detected to be dirty, during a flush.
     * Discovers inviteUserFields that should be logged by comparing the value before and after the change.
     */
    public abstract boolean onFlushDirty(Map<Object, AuditedObjectState> auditMap, Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types);

    /**
     * @param auditMap
     * @param entity
     * @param currentState
     * @param previousState
     * @param propertyNames
     */
    /**
     * @param auditMap
     * @param entity
     * @param currentState
     * @param previousState
     * @param propertyNames
     */
    public void computeChanges(Map<Object, AuditedObjectState> auditMap, Object entity, Object[] currentState,
            Object[] previousState, String[] propertyNames) {
        Map<String, String> properties = new HashMap<>();
        Map<String, Entry<String, String>> changes = new HashMap<>();
        String content = null;
        for (int i = 0; i < currentState.length; i++) {

            //discover when the property change needs to be reported
            boolean report = false;

            if (currentState[i] instanceof String && StringUtils.isBlank((String) currentState[i]) && StringUtils.isBlank((String) previousState[i])) {
                report = false;
            } else if (currentState[i] == null) {
                if (previousState[i] != null) {
                    report = true;
                }
            } else if (Collection.class.isAssignableFrom(currentState[i].getClass())) {
                report = false;
            } else if (!currentState[i].equals(previousState[i])) {
                report = true;
            }
            if (isIgnoredProperty(entity, propertyNames[i])) {
                report = false;
            }

            if (isContentProperty(entity, propertyNames[i])) {
                report = false;
                content = toString(currentState[i]);
                changes.put(propertyNames[i], new ImmutablePair<>(toString(previousState[i]), toString(currentState[i])));
            }

            //if property changed, prepare change description and keep
            if (report) {
                String previousValue = toString(previousState[i]);
                String currentValue = toString(currentState[i]);
                if (isArray(previousState[i])) {
                    previousValue = Arrays.toString((Object[]) previousState[i]);
                    currentValue = Arrays.toString((Object[]) currentState[i]);
//                        properties.put(propertyNames[i], "from <b>" + Arrays.toString((Object[]) previousState[i]) + "</b> to <b>" + Arrays.toString((Object[]) currentState[i]) + "</b>");
                } else if (isTimestamp(previousState[i])) {
                    previousValue = DatesHelper.formatDateTimeEN((LocalDateTime) previousState[i]);
                    currentValue = DatesHelper.formatDateTimeEN((LocalDateTime) currentState[i]);
                }
                properties.put(propertyNames[i], "from <b>" + previousValue + "</b> to <b>" + currentValue + "</b>");
                changes.put(propertyNames[i], new ImmutablePair<>(previousValue, currentValue));
            }
        }

        //if any changes, create an entry for the entities changes
        //if the entity is changed within the transaction before, the latest changes wins
        if (!properties.isEmpty() || StringUtils.isNotBlank(content)) {
            debug("[onFlushDirty] create an entry for the entities changes");
            if (!auditMap.containsKey(entity)) {
                auditMap.put(entity, new AuditedObjectState(properties, changes, content, Audit.AuditOperation.EDIT));
            } else {
                auditMap.get(entity).getProperties().putAll(properties);
                auditMap.get(entity).setContent(content);
            }
        }
    }

    /**
     * Checks if the propertyName of given entity is large content that should be omitted in audot log.
     * It is useful in order to hide sensitive data, eg. secrets.
     * See {@link AuditableEntity#ignorePropertiesInAudit}
     */
    protected boolean isIgnoredProperty(Object entity, String propertyName) {
        debug("[isIgnoredProperty] {}", propertyName);
        AuditableEntity e = (AuditableEntity) entity;
        return e.ignorePropertiesInAudit().contains(propertyName);
    }


    /**
     * Checks if the propertyName of given entity is large content that should be stored in {@link Audit#getContent()}
     * See {@link AuditableEntity#contentProperties}
     */
    protected boolean isContentProperty(Object entity, String propertyName) {
        debug("[isContentProperty] {}", propertyName);
        AuditableEntity e = (AuditableEntity) entity;
        return e.contentProperties().contains(propertyName);
    }

    protected boolean isArray(Object o) {
        return o != null && o.getClass().isArray();
    }

    protected boolean isTimestamp(Object o) {
        return o instanceof LocalDateTime;
    }
    
    /**
     * Naive but safe toString for object
     */
    protected String toString(Object object) {
        if (object == null) {
            return "[no value]";
        }
        if (Date.class.isAssignableFrom(object.getClass())) {
            return auditDateFormat.format((Date) object);
        }
        return object.toString();
    }
}