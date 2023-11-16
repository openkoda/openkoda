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

import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.core.helper.ApplicationContextProvider;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of Hibernate's Interceptor.
 * To make it work, it must be configured the configuration.
 * For Spring Boot, you need to set the following property in the application.properties: <br/>
 * spring.jpa.properties.hibernate.ejb.interceptor.session_scoped=com.openkoda.core.audit.PropertyChangeInterceptor
 * Important detail of the class is that there is separate object created for each hibernate session therefore
 * {@link #auditMap} is thread safe.
 *
 * @see <a href="https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/events.html">Hibernate Interceptors
 * reference</a>
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Component
public class PropertyChangeInterceptor implements Interceptor, LoggingComponent {

   private static final long serialVersionUID = 1L;


   /**
    * Map to store audit information for all entities modified within transaction.
    * Each hibernate session gets its own instance of PropertyChangeInterceptor therefor non-static field is safe.
    */
   private Map<Object, AuditedObjectState> auditMap = new ConcurrentHashMap<>();


   /**
    * Invoked by hibernate on database flush.
    * At this state we are on flush but before the commit, so we post-process the changes that should be saved in audit.
    */
   @Override
   public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames,
            Type[] types) {
      debug("[onFlushDirty]");
      return getAuditInterceptor().onFlushDirty( auditMap , entity , id , currentState , previousState , propertyNames , types );
   }

   /**
    * Invoked by hibernate on entity save. At this state we collect information about entity changes.
    */
   @Override
   public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
      debug("[onSave]");
      return getAuditInterceptor().onSave( auditMap , entity , id , state , propertyNames , types );
   }

   /**
    * Invoked by hibernate on entity delete. At this state we collect information about the deletion.
    */
   @Override
   public void onDelete(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
      debug("[onDelete]");
      getAuditInterceptor().onDelete( auditMap , entity , id , state , propertyNames , types );
   }

   /**
    * Invoked by hibernate just before transaction.
    * At this state we are ready to save the Audit in the database.
    */
   @Override
   public void beforeTransactionCompletion(Transaction tx) {
      debug("[beforeTransactionCompletion]");
      if ( !auditMap.isEmpty() ) {
         getAuditInterceptor().beforeTransactionCompletion( auditMap , tx );
      }
   }

   /**
    * Invoked by hibernate on database flush
    */
   protected AuditInterceptor getAuditInterceptor() {
      return ApplicationContextProvider.getContext().getBean( AuditInterceptor.class );
   }

}
