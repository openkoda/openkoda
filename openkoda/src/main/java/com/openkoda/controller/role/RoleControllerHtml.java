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

package com.openkoda.controller.role;

import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.form.RoleForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.openkoda.controller.common.URLConstants._HTML_ROLE;

/**
 * <p>Intended to be controller for server-side generated html actions, whereas AbstractRoleController does
 * the actual logic.</p>
 * <p>General contract is: resolve HTTP bindings, delegate work to AbstractRoleController and provide
 * ModelAndView</p>
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-24
 */
@Controller
@ResponseBody
@RequestMapping(_HTML_ROLE)
public class RoleControllerHtml extends AbstractRoleController implements HasSecurityRules {

    /**
     * Gets all roles available in the database
     */
    @PreAuthorize(CHECK_CAN_READ_BACKEND)
    @GetMapping(_ALL)
    public Object getAll(
            @Qualifier("role") Pageable rolePageable,
            @RequestParam(required = false, defaultValue = "", name = "role_search") String search) {
        debug("[getAll] search {}", search);
        return findRolesFlow(search, null, rolePageable)
                .mav(ROLE + "-" + ALL);
    }

    @PreAuthorize(CHECK_CAN_READ_BACKEND)
    @GetMapping(_ID_SETTINGS)
    public Object settings(@PathVariable(ID) Long roleId) {
        debug("[settings] roleId {}", roleId);
        return findRole(roleId)
                .mav("role-settings");
    }

    /**
     * Changes privileges for role
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(_ID_SETTINGS)
    public Object update(@PathVariable(ID) Long roleId, @Valid RoleForm roleForm, BindingResult br) {
        debug("[update] roleId {}", roleId);
        return updateRole(roleId, roleForm, br)
                .mav(ENTITY + '-' + FORMS + "::role-settings-form-success",
                        ENTITY + '-' + FORMS + "::role-settings-form-error");
    }

    /**
     * Creates view for adding new {@link com.openkoda.model.Role} to the System (along with {@link RoleForm} object
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @GetMapping(_NEW_SETTINGS)
    public Object create() {
        debug("[create]");
        return findRole(-1L)
                .mav("role-settings");
    }

    /**
     * Saves new {@link com.openkoda.model.Role} in the database
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(_NEW_SETTINGS)
    public Object saveNew(@Valid RoleForm roleForm, BindingResult br) {
        debug("[saveNew]");
        return createRole(roleForm, br)
                .mav(ENTITY + '-' + FORMS + "::role-settings-form-success",
                        ENTITY + '-' + FORMS + "::role-settings-form-error");
    }

    /**
     * Removes {@link com.openkoda.model.Role} from the database
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(_ID_REMOVE)
    public Object delete(@PathVariable(ID) Long roleId) {
        debug("[delete] roleId {}", roleId);
        return deleteRole(roleId)
                .mav( a -> true, a -> false);
    }
}
