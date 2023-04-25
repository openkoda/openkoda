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

package com.openkoda.model;

/**
 * <p>MutableUserInOrganization class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class MutableUserInOrganization {

    private Long userId;
    private Long organizationId;

    /**
     * <p>Getter for the field <code>userId</code>.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * <p>Setter for the field <code>userId</code>.</p>
     *
     * @param userId a {@link java.lang.Long} object.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * <p>Getter for the field <code>organizationId</code>.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getOrganizationId() {
        return organizationId;
    }

    /**
     * <p>Setter for the field <code>organizationId</code>.</p>
     *
     * @param organizationId a {@link java.lang.Long} object.
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "MutableUserInOrganization{" +
                "userId=" + userId +
                ", organizationId=" + organizationId +
                '}';
    }

    /**
     * <p>isPresent.</p>
     *
     * @return a boolean.
     */
    public boolean isPresent() {
        return userId != null && organizationId != null;
    }
}
