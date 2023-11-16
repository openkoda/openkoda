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

package com.openkoda.model.common;

/**
 * Interface that can be implemented by @{@link jakarta.persistence.Entity} in order to provide
 * required read/write privilege. <br/>
 * A special token that can be used in the native query is {@code ModelConstants.USER_ID_PLACEHOLDER} that
 * is replaced with id of the user querying the db.<br/>
 * Typical implementation: <br/>
 * <pre>{@code
 * @Entity Class Person implements EntityWithRequiredPrivilege {
 *     ...
 *
 *     @Formula("( CASE id WHEN " + ModelConstants.USER_ID_PLACEHOLDER + " THEN NULL ELSE '" + PrivilegeNames._readUserData + "' END )")
 *     private String requiredReadPrivilege;
 *
 *     @Formula("( '" + PrivilegeNames._manageUserData + "' )")
 *     private String requiredWritePrivilege;
 *
 *     @Override public String getRequiredReadPrivilege() {
 *         return requiredReadPrivilege;
 *     }
 *
 *     @Override
 *     public String getRequiredWritePrivilege() {
 *         return requiredWritePrivilege;
 *     }
 *
 * }
 * }</pre>
 */
public interface EntityWithRequiredPrivilege {

    String getRequiredReadPrivilege();

    String getRequiredWritePrivilege();
}
