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

/**
 *
 * <p>Implementation of Hibernate Interceptor that creates Audit Logs for selected entities.</p>
 * <br/>
 * <p>
 * The setup is following:
 * <ul>
 *   <li> Interceptor is configured in application.properties with:
 *   <code>spring.jpa.properties.hibernate.ejb.interceptor.session_scoped=com.openkoda.core.audit.PropertyChangeInterceptor</code></li>
 *   <li> The session_scoped type makes is thread safe
 *   (each hibernate session gets its own instance of the interceptor) </li>
 *   <li> To make a entity class auditable,
 *   implement {@link com.openkoda.model.common.AuditableEntityOrganizationRelated}
 *   or {@link com.openkoda.model.common.AuditableEntity}
 *   and register the class in {@link com.openkoda.core.customisation.BasicCustomisationService}</li>
 * </ul>
 * </p>
 * <p><b>Should I put a class into this package?</b></p>
 * <p>It's a closed functionality, so probably not unless you want to enhance it.</p>
 */
package com.openkoda.core.audit;