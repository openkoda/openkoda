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

package com.openkoda.dto;

import com.openkoda.model.Organization;

public class OrganizationDto implements CanonicalObject, OrganizationRelatedObject {

    public OrganizationDto() {
    }

    public OrganizationDto(Organization organization) {
        this.id = organization.getId();
        this.name = organization.getName();
        this.assignedDatasource = organization.getAssignedDatasource();
    }

    public OrganizationDto(Long id) {
        this.id = id;
    }

    public Long id;

    public String name;

    public Integer assignedDatasource;

    public boolean setupTrial;

    public OrganizationDto(Organization organization, boolean setupTrial) {
        this.id = organization.getId();
        this.name = organization.getName();
        this.setupTrial = setupTrial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String notificationMessage() {
        return String.format("Organization %s", name);
    }

    @Override
    public Long getOrganizationId() {
        return id;
    }

    public Integer getAssignedDatasource() {
        return assignedDatasource;
    }

    public void setAssignedDatasource(Integer assignedDatasource) {
        this.assignedDatasource = assignedDatasource;
    }

    public boolean isSetupTrial() {
        return setupTrial;
    }

    public void setSetupTrial(boolean setupTrial) {
        this.setupTrial = setupTrial;
    }
}