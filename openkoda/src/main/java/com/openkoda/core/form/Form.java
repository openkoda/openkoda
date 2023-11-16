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

package com.openkoda.core.form;

import com.openkoda.core.flow.PostExecuteProcessablePageAttr;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import org.springframework.validation.BindingResult;
import reactor.util.function.Tuple2;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is the base for any Form definitions in Openkoda
 * It contains fields and methods necessary to build a generic form html
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public abstract class Form implements PostExecuteProcessablePageAttr, LoggingComponentWithRequestId {

    protected final Map<FrontendMappingFieldDefinition, Tuple2<Boolean, Boolean>> readWriteForField;
    public final FrontendMappingDefinition frontendMappingDefinition;
    public static final String defaultErrorMessage = "Invalid value";
    public boolean anyWriteableField = false;

    public Form(FrontendMappingDefinition frontendMappingDefinition) {
        this.readWriteForField = new LinkedHashMap<>(frontendMappingDefinition.fields.length);
        this.frontendMappingDefinition = frontendMappingDefinition;
    }

    public abstract <F extends Form> F validate(BindingResult br);

    public final boolean canReadField(FrontendMappingFieldDefinition field) {
        Tuple2<Boolean, Boolean> value = readWriteForField.get(field);
        return value != null && value.getT1() != null && value.getT1();
    }

    public final boolean canWriteField(FrontendMappingFieldDefinition field) {
        Tuple2<Boolean, Boolean> value = readWriteForField.get(field);
        boolean canWrite = value != null && value.getT2() != null && value.getT2();
        if(!anyWriteableField && canWrite) {
            anyWriteableField = true;
        }
        return canWrite;
    }

    public FrontendMappingDefinition getFrontendMappingDefinition() {
        return frontendMappingDefinition;
    }

    public boolean requiresCodeEditor() {
        return Arrays.stream(frontendMappingDefinition.fields).anyMatch(a -> a.isCodeEditor(this));
    }
    public boolean requiresCodeEditorWithWebendpointAutocomplete(){
        return Arrays.stream(frontendMappingDefinition.fields).anyMatch(a -> a.isCodeEditorWithWebendpointAutocomplete(this));
    }
    public boolean requiresCodeEditorWithFormAutocomplete(){
        return Arrays.stream(frontendMappingDefinition.fields).anyMatch(a -> a.isCodeEditorWithFormAutocomplete(this));
    }
    public boolean requiresMap() {
        return Arrays.stream(frontendMappingDefinition.fields).anyMatch(a -> a.isMap(this));
    }

    public boolean requiresDocumentEditor() {
        return Arrays.stream(frontendMappingDefinition.fields).anyMatch(a -> a.isDocumentEditor(this));
    }

    public boolean requiresFileUpload() {
        return Arrays.stream(frontendMappingDefinition.fields).anyMatch(a -> a.isFileUpload(this));
    }

    public boolean requiresColorPicker() {
        return Arrays.stream(frontendMappingDefinition.fields).anyMatch(a -> a.isColorPicker(this));
    }

    public boolean requiresTimePicker() {
        return Arrays.stream(frontendMappingDefinition.fields).anyMatch(a -> a.isTimePicker(this));
    }

    public boolean requiresReCaptcha() {
        return Arrays.stream(frontendMappingDefinition.fields).anyMatch(a -> a.isReCaptcha(this));
    }

}
