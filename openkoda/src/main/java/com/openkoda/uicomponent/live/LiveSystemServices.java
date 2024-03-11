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

package com.openkoda.uicomponent.live;

import com.openkoda.core.customisation.ServerJSProcessRunner;
import com.openkoda.core.customisation.ServerJSRunner;
import com.openkoda.uicomponent.SystemServices;
import jakarta.inject.Inject;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


@Component
@Profile("!cloud")
public class LiveSystemServices implements SystemServices {

    @Inject ServerJSRunner serverJSRunner;

    /**
     * Runs Server side code (AKA ServerJS) by its name.
     * As the method is used in javascript flows and these are dynamic by nature,
     * there's no much need to cate about types and the method returns Object type.
     * @param serverJsName name of the server side code entity
     * @param model model for the code execution
     * @param arguments arguments for the code execution
     * @return result od the Server side code execution
     */
    @Override
    public Object runServerSideCode(String serverJsName, Map<String, Object> model, List<String> arguments) {
        return serverJSRunner.evaluateServerJsScript(serverJsName, model, arguments, Object.class);
    }

    /**
     * Executes system command and returns standard output as stream
     * @param command linux command
     */
    @Override
    public InputStream runCommandToStream(String command) {
        return ServerJSProcessRunner.commandToInputStream(command);
    }

    /**
     * Executes system command and returns standard output as String
     * @param command linux command
     */
    @Override
    public String runCommandToString(String command) {
        return ServerJSProcessRunner.commandToString(command);
    }
    /**
     * Executes system command and returns standard output as byte array
     * @param command linux command
     */
    @Override
    public byte[] runCommandToByteArray(String command) {
        return ServerJSProcessRunner.commandToByteArray(command);
    }
}
