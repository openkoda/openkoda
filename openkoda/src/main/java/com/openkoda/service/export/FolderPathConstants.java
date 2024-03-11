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

package com.openkoda.service.export;

import com.openkoda.model.component.FrontendResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FolderPathConstants {

    public static final String COMPONENTS_ = "components/";
    public static final String COMPONENTS_ADDITIONAL_FILES_ = "components-additional-files/";
    public static final String SERVER_SIDE_ = "server-side/";
    public static final String FORM_ = "form/";
    public static final String SCHEDULER_ = "scheduler/";
    public static final String EVENT_ = "event/";
    public static final String FRONTEND_RESOURCE_ = "frontend-resource/";
    public static final String UI_COMPONENT_ = "ui-component/";

    public static final String EXPORT_PATH="src/main/resources/";
    public static final String EXPORT_CODE_PATH_ = EXPORT_PATH + "code/";
    public static final String EXPORT_RESOURCES_PATH_ = EXPORT_PATH + "templates/";
    public static final String EXPORT_CONFIG_PATH_ = EXPORT_PATH + "components/";
    public static final String EXPORT_MIGRATION_PATH_ = EXPORT_PATH + "migration/";

    public static final String SUBDIR_ORGANIZATION_PREFIX = "org_";

    public static final String SERVER_SIDE_BASE_FILES_PATH = COMPONENTS_ + SERVER_SIDE_;
    public static final String FORM_BASE_FILES_PATH = COMPONENTS_ + FORM_;
    public static final String SCHEDULER_BASE_FILES_PATH = COMPONENTS_ + SCHEDULER_;
    public static final String EVENT_BASE_FILES_PATH = COMPONENTS_ + EVENT_;

    public static final String FRONTEND_RESOURCE_BASE_FILES_PATH = COMPONENTS_ + FRONTEND_RESOURCE_;
    public static final String UI_COMPONENT_BASE_FILES_PATH = COMPONENTS_ + UI_COMPONENT_;

    public static final List<String> BASE_FILE_PATHS = new ArrayList<>(Arrays.asList(
            SERVER_SIDE_BASE_FILES_PATH,
            FORM_BASE_FILES_PATH,
            SCHEDULER_BASE_FILES_PATH,
            EVENT_BASE_FILES_PATH,
            FRONTEND_RESOURCE_BASE_FILES_PATH,
            UI_COMPONENT_BASE_FILES_PATH
    ));

    public static final List<String> SUBDIR_FILE_PATHS = Arrays.stream(FrontendResource.AccessLevel.values()).map(FrontendResource.AccessLevel::getPath).toList();

}
