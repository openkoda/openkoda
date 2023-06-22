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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.openkoda.dto.file.FileDto;
import com.openkoda.form.FileForm;
import com.openkoda.model.file.File;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;

import static com.openkoda.controller.common.URLConstants._FILE;
import static com.openkoda.controller.common.URLConstants._HTML_ORGANIZATION_ORGANIZATIONID;
import static com.openkoda.core.controller.generic.AbstractController._HTML;


@Controller
@RequestMapping({_HTML_ORGANIZATION_ORGANIZATIONID + _FILE, _HTML + _FILE})
public class FileControllerHtml extends AbstractFileController {

    @PostMapping(_ID)
    @ResponseBody
    public Object update(
            @RequestParam String content,
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(ID) long fileId) {
        debug("[update] fileId: {}", fileId);
        return updateFile()
                .mav(a -> true, a -> a.get(message));
    }

    @PostMapping(_ID + "/rescale")
    @ResponseBody
    public Object rescale(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(ID) long fileId,
            @RequestParam("w") int width) {
        debug("[rescale] fileId: {}", fileId);
        return rescaleFile(fileId, width)
                .mav(a -> "Done. Refresh the page.", a -> a.get(message));
    }


    @GetMapping(_ALL)
    public Object getAll(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @Qualifier("file") Pageable filePageable,
            @RequestParam(required = false, defaultValue = "", name = "file_search") String search) {
        debug("[getAll]");
        return searchFile(organizationId, search, null, filePageable)
                .mav("file-" + ALL);
    }

    @GetMapping(_NEW_SETTINGS)
    public Object create(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId) {
        debug("[create]");
        return findFile(organizationId, -1L)
                .mav("file-settings");
    }


    @PostMapping(_NEW_SETTINGS)
    public Object saveNew(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @Valid FileForm fileForm, BindingResult br) {
        debug("[saveNew]");
        return createFile(organizationId, fileForm, br)
                .mav("file-entity-form::file-settings-form-success",
                        "file-entity-form::file-settings-form-error");
    }

    @GetMapping(_ID_SETTINGS)
    public Object settings(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(ID) Long fileId) {
        debug("[settings] fileId: {}", fileId);
        return findFile(organizationId, fileId)
                .mav("file-settings");
    }

    @PostMapping(_ID_SETTINGS)
    public Object save(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(ID) Long fileId,
            @Valid FileForm fileForm, BindingResult br) {
        debug("[save] fileId: {}", fileId);
        return updateFile(organizationId, fileId, fileForm, br)
                .mav("file-entity-form::file-settings-form-success",
                        "file-entity-form::file-settings-form-error");
    }

    @PostMapping(_ID_REMOVE)
    @ResponseBody
    public Object remove(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(ID) Long fileId) {
        debug("[remove] fileId {}", fileId);
        return removeFile(fileId)
                .mav(a -> true, a -> false);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class UploadResponse implements Serializable {

        public String error;
        public boolean success;
        public FileDto file;
        public Long fileId;

        public UploadResponse(String error, boolean success, Long fileId, FileDto file) {
            this.error = error;
            this.success = success;
            this.file = file;
            this.fileId = fileId;
        }
    }

    @Transactional(readOnly = true)
    @GetMapping(_ID + _CONTENT)
    public void content(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(ID) Long fileId,
            HttpServletResponse response) throws IOException, SQLException {
        debug("[content] fileId {}", fileId);
        File f = secureFileRepository.findOne(fileId);
        services.file.getFileContentAndPrepareResponse(f, true, response);
    }

    @Transactional
    @PostMapping(_NEW + _UPLOAD)
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    public ResponseEntity<UploadResponse> upload(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @RequestParam("qqfile") MultipartFile file,
            @RequestParam("qquuid") String uuid,
            @RequestParam("qqfilename") String fileName,
            @RequestParam(value = "qqpartbyteoffset", required = false, defaultValue = "-1") long partByteOffset,
            @RequestParam(value = "qqpartindex", required = false, defaultValue = "-1") int partIndex,
            @RequestParam(value = "qqtotalparts", required = false, defaultValue = "-1") int totalParts,
            @RequestParam(value = "qqtotalfilesize", required = false, defaultValue = "-1") long totalFileSize) throws SQLException, IOException {
        debug("[upload] upload uuid {}, fileName {}", uuid, fileName);
        File f = unsecureFileRepository.findByUploadUuid(uuid);
        if (f == null) {
            String originalFilename = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            f = services.file.saveAndPrepareFileEntity(organizationId, uuid, fileName, totalFileSize, originalFilename, inputStream);
            unsecureFileRepository.saveAndFlush(f);
        }
        UploadResponse result = new UploadResponse(null, true, f.getId(), File.toFileDto(f));
        return ResponseEntity.ok().body(result);
    }

    @PostMapping(_NEW + "/upload-done")
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    public ResponseEntity<DefaultMapEntry> chunksDone(
            @RequestParam("qquuid") String uuid) {
        debug("[chunksDone] upload uuid {}", uuid);
        File f = unsecureFileRepository.findByUploadUuid(uuid);
        DefaultMapEntry dto = new DefaultMapEntry(f.getId(), new FileDto());
        return ResponseEntity.ok().body(dto);
    }
}
