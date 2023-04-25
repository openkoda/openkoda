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

package com.openkoda.form;

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.Form;
import com.openkoda.dto.file.FileDto;
import com.openkoda.model.file.File;
import org.springframework.validation.BindingResult;

public class FileForm extends AbstractOrganizationRelatedEntityForm<FileDto, File> {

    public FileForm() {
        super(FileFrontendMappingDefinitions.fileForm);
    }

    public FileForm(Long organizationId, File entity) {
        super(organizationId, new FileDto(), entity, FileFrontendMappingDefinitions.fileForm);
    }

    @Override
    public FileForm populateFrom(File entity) {
        
        dto.filename = entity.getFilename();
        dto.publicFile = entity.isPublicFile();
        dto.contentType = entity.getContentType();
        return this;
    }

    @Override
    protected File populateTo(File entity) {
        
        entity.setFilename(getSafeValue(entity.getFilename(), FileFrontendMappingDefinitions.FILENAME_));
        entity.setPublicFile(getSafeValue(entity.isPublicFile(), FileFrontendMappingDefinitions.PUBLIC_FILE_));
        entity.setContentType(getSafeValue(entity.getContentType(), FileFrontendMappingDefinitions.CONTENT_TYPE_));
        return entity;
    }

    @Override
    public <F extends Form> F validate(BindingResult br) {
        return null;
    }
}