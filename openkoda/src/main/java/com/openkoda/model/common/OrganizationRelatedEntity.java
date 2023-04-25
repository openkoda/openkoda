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

package com.openkoda.model.common;

import com.openkoda.dto.OrganizationRelatedObject;

/**
 * <p>OrganizationRelatedEntity interface.</p>
 * <p>Non organization related entities are: User, Token, Role, ReadNotification, FacebookUser, GoogleUser, LDAPUser, LoginAndPassword</p>
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
public interface OrganizationRelatedEntity extends OrganizationRelatedObject, LongIdEntity {
    /**
     * <p>getOrganizationId.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    Long getOrganizationId();

    /**
     * Use @{@link org.hibernate.annotations.Formula} to implement!
     */
    String getReferenceString();

}
