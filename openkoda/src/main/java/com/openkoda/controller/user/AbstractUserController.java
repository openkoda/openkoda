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

package com.openkoda.controller.user;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.customisation.BasicCustomisationService;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.flow.ResultAndModel;
import com.openkoda.core.helper.UserHelper;
import com.openkoda.core.repository.common.ProfileSettingsRepository;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.service.event.ApplicationEvent;
import com.openkoda.form.EditUserForm;
import com.openkoda.model.Role;
import com.openkoda.model.User;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuple5;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.function.Function;

import static com.openkoda.controller.common.SessionData.SPOOFING_USER;


/**
 * <p>AbstractUserController class.</p>
 * <p>Controller that provides actual User related functionality for different type of access (eg. API, HTML)</p>
 * <p>Implementing classes should take over http binding and forming a result whereas this controller should take care
 * of actual implementation</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class AbstractUserController extends AbstractController {

    @Inject
    private BasicCustomisationService customisationService;

    @Inject
    private UserHelper userHelper;

    protected PageModelMap findUsers(String aSearchTerm, Specification<User> aSpecification, Pageable
            aPageable) {
        debug("[findUsers] search {}", aSearchTerm);
        return Flow.init()
                .thenSet( userPage, a -> repositories.secure.user.search(aSearchTerm, null, aSpecification, aPageable))
                .execute();
    }

    protected PageModelMap getUsersProfile(Long userId){
        return findUser(userId)
                .thenSet(additionalSettingsForms, a -> prepareAdditionalForms(a, userId))
                .execute();
    }

    protected Flow<Long, EditUserForm, AbstractUserController> findUser(Long id) {
        debug("[findUser] userId {}", id);
        return Flow.init(this, id)
                .thenSet(userEntity, a -> repositories.secure.user.findOne(id))
                .then( a -> services.validation.assertNotNull(a.result, HttpStatus.UNAUTHORIZED))
                .thenSet(editUserForm, a -> new EditUserForm(a.result));

    }

    private List<Tuple5<ProfileSettingsRepository, Function, PageAttr, String, String>> prepareAdditionalForms(ResultAndModel a, Long userId) {

        for (Tuple4<ProfileSettingsRepository, Function, PageAttr, String> t : customisationService.additionalSettingsForms) {
            Object entity = t.getT1().findOneForUserId(userId);
            Object form = t.getT2().apply(entity);
            a.model.put(t.getT3(), form);
        }
        return customisationService.additionalSettingsForms;

    }
    protected PageModelMap spoofUser(Long userId, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        // spoofingUserId is an ID of a User who is a global admin or someone that can impersonate other users
        // on spoofing exit this spoofingUserId will be used to log back into one's account
        long spoofingUserId = userHelper.getUserId();
        return Flow.init()
                .thenSet(userEntity, a -> repositories.unsecure.user.findOne(userId))
                .then(a -> services.runAs.startRunAsUser(a.result, request, response))
                .then(a -> {
                    if(a.result) {
                        session.setAttribute(SPOOFING_USER, spoofingUserId);
                    }
                    return a.result;
                })
                .thenSet(organizationEntityId, a -> a.model.get(userEntity).getOrganizationIds().length > 0 ? a.model.get(userEntity).getOrganizationIds()[0] : null)
                .execute();
    }
    protected PageModelMap stopSpoofingUser(HttpSession session, HttpServletRequest request, HttpServletResponse response){
        return Flow.init()
                .then(a -> services.runAs.exitRunAsUser((Long) session.getAttribute(SPOOFING_USER), request, response))
                .execute();
    }
    protected PageModelMap saveUser(Long id, EditUserForm userFormData, BindingResult br) {
        debug("[saveUser] userId {}", id);
        return Flow.init(transactional)
                .thenSet( editUserForm, a -> userFormData)
                .thenSet( roleEntity, a -> repositories.unsecure.role.findByName(userFormData.dto.getGlobalRoleName()))
                .thenSet( userEntity, a -> repositories.secure.user.findOne(id))
                .then( a -> services.validation.assertNotNull(a.result, HttpStatus.UNAUTHORIZED))
                .then( a -> services.validation.validateAndPopulateToEntity(userFormData, br,a.result))
                .then( a -> repositories.unsecure.user.save(a.result))
                .then( a -> userFormData.dto.globalRoleName != null ? services.user.changeUserGlobalRole(a.result, userFormData.dto.getGlobalRoleName()) : null)
                .then( a -> services.applicationEvent.emitEvent(ApplicationEvent.USER_MODIFIED, a.model.get(userEntity).getBasicUser()))
                .thenSet(editUserForm, a -> {
                    EditUserForm form = new EditUserForm(a.model.get(userEntity));
                    Role role = a.model.get(roleEntity);
                    if(role != null) {
                        form.dto.setGlobalRoleName(role.getName());
                    }
                    return form;
                })
                .execute();
    }

    protected PageModelMap deleteUser(Long id) {
        debug("[deleteUser] userId {}", id);
        return Flow.init(this, id)
                .then(a -> repositories.unsecure.user.deleteOne(a.result)).execute();
    }

    protected PageModelMap doResetApiKey(){
        return Flow.init(userEntity, repositories.secure.user.findOne(UserProvider.getUserIdOrNotExistingId()))
                .thenSet(userEntity, apiKeyEntity, plainApiKeyString, a -> services.apiKey.resetApiKey(a.result))
                .then(a -> Tuples.of(
                        repositories.unsecure.user.save(a.result.getT1()),
                        repositories.unsecure.apiKey.save(a.result.getT2())))
                .execute();
    }

}
