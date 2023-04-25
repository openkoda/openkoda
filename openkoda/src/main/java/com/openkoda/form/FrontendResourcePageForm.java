/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice 
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.form;

import com.openkoda.core.helper.ReadableCode;
import com.openkoda.core.helper.UrlHelper;
import com.openkoda.dto.CmsPageDto;
import com.openkoda.dto.file.FileDto;
import com.openkoda.model.FrontendResource;
import com.openkoda.model.file.File;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.regex.Pattern;

public class FrontendResourcePageForm extends FrontendResourceForm<CmsPageDto> implements ReadableCode {

    public FrontendResourcePageForm() {
        super(FrontendMappingDefinitions.frontendResourcePageForm);
    }

    public FrontendResourcePageForm(Long organizationId, FrontendResource frontendResource) {
        super(organizationId, new CmsPageDto(), frontendResource, FrontendMappingDefinitions.frontendResourcePageForm);
    }

    @Override
    public FrontendResourcePageForm validate(BindingResult br) {
        if (StringUtils.isBlank(dto.urlPath)) {
            br.rejectValue("dto.urlPath", "not.empty");
        }
        if (not(Pattern.matches("[a-z\\-\\/0-9]+", dto.urlPath))) {
            br.rejectValue("dto.urlPath", "simple.path");
        }
        if (StringUtils.startsWith(dto.urlPath, "/")) {
            br.rejectValue("dto.urlPath", "not.slash.prefix");
        }
        return this;
    }

    public static FileDto toFileDto(File a) {
        return new FileDto(a.getId(), a.getOrganizationId(), a.getFilename(), a.getContentType(), UrlHelper.getPublicFileURL(a));
    }

    @Override
    protected FrontendResource populateTo(FrontendResource entity) {
        entity.setUrlPath(getSafeValue(entity.getUrlPath(), URL_PATH_, nullIfBlank));
        entity.setName(getSafeValue(entity.getName(), URL_PATH_));
        entity.setType(FrontendResource.Type.PAGE);
        entity.setIncludeInSitemap(true);
        return entity;
    }
}