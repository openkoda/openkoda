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

import com.openkoda.model.common.Audit;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Keeps information about changes in an object being committed into the database.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class AuditedObjectState {

   //Map of: propertyName -> change description
   private final Map<String, String> properties;

   //Map of: propertyName -> (value before change, value after change)
   private final Map<String, Entry<String, String>> changes;

   //Type of the operation
   private Audit.AuditOperation operation;

   //Optional content of property
   private String content;


   /**
    * Constructor for audited object state information
    * @param properties properties of the object being audited
    * @param changes changed detected in the object
    * @param operation operation type, see {@link com.openkoda.model.common.Audit.AuditOperation}
    */
   public AuditedObjectState(Map<String, String> properties, Map<String, Entry<String, String>> changes, Audit.AuditOperation operation) {
      super();
      this.properties = properties;
      this.changes = changes;
      this.operation = operation;
   }

   /**
    * Constructor for audited object state information
    * @param properties properties of the object being audited
    * @param changes changed detected in the object
    * @param content additional large content to be stored in audit.
    * @param operation operation type, see {@link com.openkoda.model.common.Audit.AuditOperation}
    */
   public AuditedObjectState(Map<String, String> properties, Map<String, Entry<String, String>> changes, String content, Audit.AuditOperation operation) {
      super();
      this.properties = properties;
      this.changes = changes;
      this.content = content;
      this.operation = operation;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
        this.content = content;
    }

   public Map<String, Entry<String, String>> getChanges() {
      return changes;
   }

   public void setOperation(Audit.AuditOperation operation) {
      this.operation = operation;
   }

   public Audit.AuditOperation getOperation() {
      return operation;
   }
}
