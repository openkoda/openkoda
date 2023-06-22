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

package com.openkoda.controller.file;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.form.FileForm;
import com.openkoda.model.file.File;
import com.openkoda.repository.file.FileRepository;
import com.openkoda.repository.file.SecureFileRepository;
import jakarta.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;


public class AbstractFileController extends AbstractController implements HasSecurityRules {

    @Inject
    SecureFileRepository secureFileRepository;

    @Inject
    FileRepository unsecureFileRepository;

    static PageAttr<File> fileEntity = new PageAttr<>("fileEntity");
    static PageAttr<FileForm> fileForm = new PageAttr<>("fileForm");
    static PageAttr<Page<File>> filePage = new PageAttr<>("filePage");

    public final static String fileUrl = "file";


    protected PageModelMap searchFile(
            Long organizationId,
            String aSearchTerm,
            Specification<File> aSpecification,
            Pageable aPageable) {
        debug("[searchFile]");
        return Flow.init()
            .thenSet(filePage, a -> secureFileRepository.search(aSearchTerm, organizationId, aSpecification, aPageable))
            .execute();
    }


    protected PageModelMap findFile(Long organizationId, long fileId) {
        debug("[findFile] fileId: {}", fileId);
        return Flow.init(fileId)
            .thenSet(fileEntity, a -> secureFileRepository.findOne(fileId))
            .thenSet(fileForm, a -> new FileForm(organizationId, a.result))
            .execute();
    }


    protected PageModelMap updateFile(Long organizationId, long fileId, FileForm formData, BindingResult br) {
        debug("[updateFile] fileId: {}", fileId);
        return Flow.init(fileForm, formData)
            .then(a -> secureFileRepository.findOne(fileId))
            .then(a -> services.validation.validateAndPopulateToEntity(formData, br,a.result))
            .thenSet(fileEntity, a -> unsecureFileRepository.save(a.result))
            .execute();
    }


    protected PageModelMap createFile(Long organizationId, FileForm formData, BindingResult br) {
        debug("[createFile]");
        return Flow.init(fileForm, formData)
            .then(a -> services.validation.validateAndPopulateToEntity(formData, br,new File(organizationId)))
            .then(a -> unsecureFileRepository.save(a.result))
            .thenSet(fileForm, a -> new FileForm())
            .execute();
    }


    protected PageModelMap removeFile(long fileId) {
        debug("[removeFile] fileId: {}", fileId);
        return Flow.init(transactional)
            .then(a -> unsecureFileRepository.removeFileReference(fileId))
            .then(a -> unsecureFileRepository.removeFile(fileId))
            .execute();
    }

    protected PageModelMap updateFile(){
        return Flow.init(transactional)
                .execute();
    }

    protected PageModelMap rescaleFile(long fileId, int width){
        return Flow.init(transactional)
                .then(a -> secureFileRepository.findOne(fileId))
                .then(a -> services.file.scaleImage(a.result, width))
                .then(a -> repositories.unsecure.file.saveAndFlush(a.result))
                .execute();
    }

}
