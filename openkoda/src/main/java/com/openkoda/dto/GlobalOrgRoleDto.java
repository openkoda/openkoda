package com.openkoda.dto;

import java.util.ArrayList;
import java.util.List;

public class GlobalOrgRoleDto {

    public List<String> globalOrganizationRoles = new ArrayList<>();

    public List<String> getGlobalOrganizationRoles() {
        return globalOrganizationRoles;
    }

    public void setGlobalOrganizationRoles(List<String> globalOrganizationRoles) {
        this.globalOrganizationRoles = globalOrganizationRoles;
    }
}
