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

package com.openkoda.core.service;

import com.openkoda.core.exception.NotFoundException;
import com.openkoda.core.exception.ServerErrorException;
import com.openkoda.core.exception.UnauthorizedException;
import com.openkoda.core.flow.ValidationException;
import com.openkoda.core.form.AbstractEntityForm;
import com.openkoda.core.form.AbstractForm;
import com.openkoda.core.form.Form;
import com.openkoda.core.form.FrontendMappingFieldDefinition;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.common.LongIdEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import reactor.util.function.Tuple2;

import java.util.Map;
import java.util.function.Function;

import static com.openkoda.controller.common.URLConstants.CAPTCHA_VERIFIED;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */

@Service
public class ValidationService implements LoggingComponentWithRequestId {

    final public <F extends AbstractEntityForm<D, E>, E extends LongIdEntity, D> E validateAndPopulateToEntity(F form, BindingResult br, E entity) {
        try {
            validate(form, br);
        } catch (ValidationException ve) {
            form.prepareFieldsReadWritePrivileges(entity);
            if (br.hasErrors()) {
                for (FieldError fe : br.getFieldErrors()) {
                    String name = form.extractFieldName(fe.getField());
                    FrontendMappingFieldDefinition fd = form.getFrontendMappingDefinition().findField(name);
                    if (!form.canWriteField(fd)) {
                        throw new ValidationException("Contact the administrator");
                    }
                }
            }
            throw ve;
        }
        return form.populateToEntity(entity);
    }

    /**
     * <p>validate.</p>
     *
     * @param form a T object.
     * @param br a {@link org.springframework.validation.BindingResult} object.
     * @param <T> a T object.
     * @return a T object.
     */
    final public <T extends AbstractForm> T validate(T form, BindingResult br) {
        debug("[validate]");
        for (Tuple2<FrontendMappingFieldDefinition, Function<Object, String>> fieldValidator : form.frontendMappingDefinition.fieldValidators) {
             form.validateField(fieldValidator.getT1(), fieldValidator.getT2(), br);
        }

        for (Function<? extends Form, Map<String, String>> formValidator : form.frontendMappingDefinition.formValidators) {
            Map<String, String> rejections = ((Function<T, Map<String, String>>) formValidator).apply(form);
            if (rejections == null) {
                continue;
            }
            for (Map.Entry<String, String> e : rejections.entrySet()) {
                br.rejectValue(e.getKey(), e.getValue());
            }
        }

        form.validate(br);
        if(form.requiresReCaptcha() && !isCaptchaVerified()){
            throw new ValidationException("Validation error");
        }
        if (br.hasErrors()) {
            throw new ValidationException("Validation error");
        }
        return form;
    }

    public boolean isCaptchaVerified(){
        if(RequestContextHolder.getRequestAttributes() != null){
            RequestAttributes attr = RequestContextHolder.getRequestAttributes();
            Object obj = attr.getAttribute(CAPTCHA_VERIFIED, 0);
            return (Boolean) obj;
        }else{
            return false;
        }
    }

    public <T> T assertNotNull(T obj, HttpStatus statusOnFail) {
        if (obj == null) {
            switch (statusOnFail) {
                case NOT_FOUND -> throw new NotFoundException();
                case UNAUTHORIZED -> throw new UnauthorizedException();
                default -> throw new ServerErrorException();
            }
        }
        return obj;
    }
    public <T> T assertNotNull(T obj) {
        return assertNotNull(obj, HttpStatus.NOT_FOUND);
    }

    final public <F extends AbstractEntityForm<D, E>, E extends LongIdEntity, D> boolean validateAndPopulateToEntity(F form, E entity) {
        boolean isValid;
        try {
            isValid = validate(form);
        } catch (ValidationException ve) {
            form.prepareFieldsReadWritePrivileges(entity);
            throw ve;
        }
        if(isValid) {
            form.populateToEntity(entity);
        }
        return isValid;
    }

    final public <T extends AbstractForm> boolean validate(T form) {
        debug("[validate]");
        for (Tuple2<FrontendMappingFieldDefinition, Function<Object, String>> fieldValidator : form.frontendMappingDefinition.fieldValidators) {
            if(!form.validateField(fieldValidator.getT1(), fieldValidator.getT2())){
                return false;
            }
        }

        for (Function<? extends Form, Map<String, String>> formValidator : form.frontendMappingDefinition.formValidators) {
            Map<String, String> rejections = ((Function<T, Map<String, String>>) formValidator).apply(form);
            if(rejections != null){
                return false;
            }

        }
        return true;
        //form.validate(br); -- skip validate method for the form extension for now (only validators in FrontendMappingDefinition are considered)
    }
}
