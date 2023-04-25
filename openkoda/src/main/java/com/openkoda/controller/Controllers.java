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

package com.openkoda.controller;

import com.openkoda.controller.admin.AuditController;
import com.openkoda.controller.organization.OrganizationControllerHtml;
import com.openkoda.controller.user.UserControllerHtml;
import com.openkoda.core.controller.frontendresource.FrontendResourceControllerHtml;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

/**
 * Class that aggregates (by injections) all Controllers.
 * Feel free to add more controllers here.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Component("AllControllers")
public class Controllers {

    @Inject
    public OrganizationControllerHtml organization;

    @Inject
    public UserControllerHtml user;

    @Inject
    public AuditController audit;

    @Inject
    public FrontendResourceControllerHtml frontendResource;

    @Inject
    public CRUDControllerConfigurationMap crudControllerConfigurationMap;



}
