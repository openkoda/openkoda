/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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

import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.OrganizationUserDetailsService;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.*;
import com.openkoda.model.authentication.FacebookUser;
import com.openkoda.model.authentication.GoogleUser;
import com.openkoda.model.authentication.LDAPUser;
import com.openkoda.model.authentication.LoginAndPassword;
import com.openkoda.model.common.Audit;
import com.openkoda.model.common.AuditableEntity;
import com.openkoda.model.event.EventListenerEntry;
import com.openkoda.model.task.Email;
import com.openkoda.model.task.HttpRequestTask;
import com.openkoda.model.task.Task;
import com.openkoda.repository.admin.AuditRepository;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

/**
 * Interception Service that creates Audit logs on pre-commit stage for selected entities.
 * The entities being intercepted on change are kept in auditListeners, and can be extended with
 * {@link com.openkoda.core.customisation.BasicCustomisationService}
 * <p>
 * See also: https://docs.jboss.org/hibernate/orm/3.5/api/org/hibernate/Interceptor.html
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Service
public class AuditInterceptor implements LoggingComponentWithRequestId {

    private static final FastDateFormat auditDateFormat = FastDateFormat.getInstance("dd/MM/yyyy HH:mm:ss");

    //Keeps classes to investigate in Audit log
    //Map of: Entity class under investigation -> Property change listener for that class
    private Map<Class<? extends AuditableEntity>, PropertyChangeListener> auditListeners;

    @Inject
    private AuditRepository auditRepository;
    @Inject
    private OrganizationUserDetailsService userService;

    public AuditInterceptor() {
        //standard listeners, can be extended with @{@link com.openkoda.core.customisation.BasicCustomisationService}
        auditListeners = new HashMap<>();
        registerAuditableClass(Task.class, "Task");
        registerAuditableClass(Organization.class, "Organization");
        registerAuditableClass(UserRole.class, "User Role");
        registerAuditableClass(User.class, "User");
        registerAuditableClass(Role.class, "Role");
        registerAuditableClass(OrganizationRole.class, "Organization Role");
        registerAuditableClass(GlobalRole.class, "Global Role");
        registerAuditableClass(FrontendResource.class, "Frontend Resource");
        registerAuditableClass(ControllerEndpoint.class, "Controller Endpoint");
        registerAuditableClass(Email.class, "Email");
        registerAuditableClass(HttpRequestTask.class, "Http Request Task");
        registerAuditableClass(EventListenerEntry.class, "Event Listener");
        registerAuditableClass(FacebookUser.class, "Facebook User");
        registerAuditableClass(GoogleUser.class, "Google User");
        registerAuditableClass(LDAPUser.class, "LDAP User");
        registerAuditableClass(LoginAndPassword.class, "Regular User");
    }

    /**
     * Registers class to create Audit logs.
     */
    public <T extends AuditableEntity> PropertyChangeListener registerAuditableClass(Class<T> c, String classLabel) {
        debug("[registerAuditableClass] {}", classLabel);
        PropertyChangeListener changeListener = new PropertyChangeListener(c.getSimpleName(), classLabel);
        auditListeners.put(c, changeListener);
        return changeListener;
    }

    /**
     * Called when an object is detected to be dirty, during a flush.
     * Discovers inviteUserFields that should be logged by comparing the value before and after the change.
     */
    @SuppressWarnings("unchecked")
    public boolean onFlushDirty(Map<Object, AuditedObjectState> auditMap, Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames,
                                Type[] types) {
        debug("[onFlushDirty] entity {} id {}", entity, id);
        PropertyChangeListener listener = auditListeners.get(entity.getClass());

        //skip if no listener for the entity type
        if (isEntitySpecificListenerRegistered(listener)) {
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
                    if (isArray(previousState[i])) {
                        properties.put(propertyNames[i], "from <b>" + Arrays.toString((Object[]) previousState[i]) + "</b> to <b>" + Arrays.toString((Object[]) currentState[i]) + "</b>");
                    } else {
                        properties.put(propertyNames[i], "from <b>" + toString(previousState[i]) + "</b> to <b>" + toString(currentState[i]) + "</b>");
                    }
                    changes.put(propertyNames[i], new ImmutablePair<>(toString(previousState[i]), toString(currentState[i])));
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
        return false;
    }

    private boolean isArray(Object o) {
        return o != null && o.getClass().isArray();
    }


    /**
     * Checks whether the listener is not null AKA if it was registered for an entity
     */
    private boolean isEntitySpecificListenerRegistered(PropertyChangeListener listener) {
        return listener != null;
    }

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
    @SuppressWarnings("unchecked")

    public boolean onSave(Map<Object, AuditedObjectState> auditMap, Object entity, Object id, Object[] entityState, String[] propertyNames, Type[] types) {
        debug("[onSave] entity {} id {}", entity, id);
        PropertyChangeListener listener = auditListeners.get(entity.getClass());

        //skip if no listener for the entity type
        if (isEntitySpecificListenerRegistered(listener)) {
            Map<String, String> properties = new HashMap<>();
            Map<String, Entry<String, String>> changes = new HashMap<>();
            String content = null;
            for (int i = 0; i < entityState.length; i++) {
                if (entityState[i] == null) {
                    continue;
                }
                if (Collection.class.isAssignableFrom(entityState[i].getClass())) {
                    continue;
                }

                if (entityState[i] instanceof String && StringUtils.isBlank((String) entityState[i])) {
                    continue;
                }

                if (isContentProperty(entity, propertyNames[i])) {
                    content = toString(entityState[i]);
                    changes.put(propertyNames[i], new ImmutablePair<>("", toString(entityState[i])));
                    continue;
                }

                if (isIgnoredProperty(entity, propertyNames[i])) {
                    continue;
                }
                properties.put(propertyNames[i], toString(entityState[i]));
                changes.put(propertyNames[i], new ImmutablePair<>("", toString(entityState[i])));
            }
            auditMap.put(entity, new AuditedObjectState(properties, changes, content, Audit.AuditOperation.ADD));
            debug("[onSave] auditMap updated");
        }
        return false;
    }

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
    public void onDelete(Map<Object, AuditedObjectState> auditMap, Object entity, Object id, Object[] entityState, String[] propertyNames, Type[] types) {
        debug("[onDelete] entity {} id {}", entity, id);
        PropertyChangeListener listener = auditListeners.get(entity.getClass());

        //skip if no listener for the entity type
        if (isEntitySpecificListenerRegistered(listener)) {
            auditMap.put(entity, new AuditedObjectState(new HashMap(), new HashMap(), Audit.AuditOperation.DELETE));
            debug("[onDelete] auditMap updated");
        }
    }


    /**
     * Method invoked at the last stage of transaction completion, therefor ideal for saving audit information.
     * If constructs {@link Audit} objects from auditMap and saves then to the database using the ongoing transaction tx.
     * Important - if multiple changes on same field during one session, the
     * saved will be the last change on the entity, because of that there is a
     * possibility that the old state differs from previous audit log new state.
     * Also - the routine of constructing and saving the audit log should be kept in a good shape, as any error can
     * revert the actual transaction.
     */
    public void beforeTransactionCompletion(Map<Object, AuditedObjectState> auditMap, Transaction tx) {
        try {
            debug("[beforeTransactionCompletion]");
            Optional<OrganizationUser> user = UserProvider.getFromContext();
            auditMap.forEach((k, v) -> {
                Audit[] audits = auditListeners.get(k.getClass()).prepareAuditLogs(k, user, v, user.map(OrganizationUser::getRolesInfo));
                auditRepository.saveAll(new ArrayList(Arrays.asList(audits)));
                auditRepository.flush();
            });
        } finally {
            // do we need this?
            auditMap.clear();
        }
    }

    /**
     * Checks if the propertyName of given entity is large content that should be omitted in audot log.
     * It is useful in order to hide sensitive data, eg. secrets.
     * See {@link AuditableEntity#ignorePropertiesInAudit}
     */
    private boolean isIgnoredProperty(Object entity, String propertyName) {
        debug("[isIgnoredProperty] {}", propertyName);
        AuditableEntity e = (AuditableEntity) entity;
        return e.ignorePropertiesInAudit().contains(propertyName);
    }


    /**
     * Checks if the propertyName of given entity is large content that should be stored in {@link Audit#getContent()}
     * See {@link AuditableEntity#contentProperties}
     */
    private boolean isContentProperty(Object entity, String propertyName) {
        debug("[isContentProperty] {}", propertyName);
        AuditableEntity e = (AuditableEntity) entity;
        return e.contentProperties().contains(propertyName);
    }

    /**
     * Naive but safe toString for object
     */
    private String toString(Object object) {
        if (object == null) {
            return "[no value]";
        }
        if (Date.class.isAssignableFrom(object.getClass())) {
            return auditDateFormat.format((Date) object);
        }
        return object.toString();
    }
}
