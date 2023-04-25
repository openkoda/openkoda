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

package com.openkoda.dto.payment;

import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.OrganizationRelatedObject;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-10-11
 */
public class PlanDto  implements CanonicalObject, OrganizationRelatedObject {

    public Long organizationId;
    public String planName;

    public PlanDto() {
    }

    public PlanDto(Long organizationId) {
        this.organizationId = organizationId;
        this.planName = "";
    }

    public PlanDto(Long organizationId, String planName) {
        this.organizationId = organizationId;
        this.planName = planName;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    @Override
    public String notificationMessage() {
        return String.format("Plan %s for organization %s.", planName, organizationId);
    }

}
