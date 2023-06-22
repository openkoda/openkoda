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

package com.openkoda.core.customisation;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.dto.system.ScheduledSchedulerDto;
import com.openkoda.model.ServerJs;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.output.NullWriter;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class creates a Consumer that can execute JS scripts created as FrontendResources.
 */
@Service
public class ServerJSRunner extends ComponentProvider {
    public static final List<LoggingFutureWrapper> manuallyStartedThreads = new ArrayList<>();
    private static final ExecutorService manuallyStartedThreadsPool = Executors.newFixedThreadPool(4);

    /**
     * Context of the script engine.
     * In this case it's JS with unlimited privileges.
     */
    private Context.Builder contextBuilder = Context.newBuilder("js")
            .allowHostAccess(HostAccess.ALL)
            .allowAllAccess(true)
            .allowHostClassLoading(true)
            .allowHostClassLookup(className -> true);  //allows access to all Java classes


    /**
     * Starts a Server-side JS script
     * @param script JS script
     * @param model JSON object that represents map that are provided as model to the script execution context
     * @param arguments NEW-LINE separated list of arguments that are provided
     *                 as 'arguments' variable to the script execution context
     * @param log log writer
     * @return object returned by the script execution
     */
    public Object startServerJs(String script, String model, String arguments, Writer log) {
        try {
            //we create temporary serverJs instance to deserialize model into map
            //that can be improved
            ServerJs serverJs = new ServerJs(script, model, arguments);
            return evaluateScript(script, serverJs.getModelMap(), Object.class, log);
        } catch (Exception e) {
            return Collections.singletonMap(PageAttributes.error.name, e.getMessage());
        } finally {
            try {
                log.close();
            } catch (IOException e) {
                error("[manuallyStartServerJs]", e);
            }
        }
    }

    /**
     * Starts a Server-side JS script in response to {@link com.openkoda.core.service.event.ApplicationEvent#SCHEDULER_EXECUTED}
     */
    public Object startScheduledServerJs(ScheduledSchedulerDto dto, String schedulerData, String serverJsName, String argument1, String argument2) {
        if (not(StringUtils.equals(dto.eventData, schedulerData))) {
            debug("[startScheduledServerJs] not this event. Exiting");
            return null;
        }
        ServerJs serverJs = repositories.unsecure.serverJs.findByName(serverJsName);
        return evaluateServerJsScript(serverJs, null, Arrays.asList(schedulerData, serverJsName, argument1, argument2), Object.class);
    }

    /**
     * Starts a Server-side JS script with  {@link CustomisationService}
     */
    public Object startCustomisationServerJs(LocalDateTime startDateTime, String serverJsName, String argument1, String argument2, String argument3) {
        ServerJs serverJs = repositories.unsecure.serverJs.findByName(serverJsName);
        return evaluateServerJsScript(serverJs, null, Arrays.asList(serverJsName, argument1, argument2, argument3), Object.class);
    }

    public <T> T evaluateServerJsScript(String serverJsName, Map<String, Object> externalModel, List<String> externalArguments, Class<T> resultType) {
        return evaluateServerJsScript(repositories.unsecure.serverJs.findByName(serverJsName), externalModel, externalArguments, resultType);
    }

    /**
     * Executes a Server-side JS script
     * @param serverJs ServerJs object
     * @param externalModel map added to the default serverJS model (and overrides the default entries)
     * @param externalArguments arguments added to the default serverJS arguments (and overrides the default values)
     * @param resultType expected type of the script result
     * @return object returned by the script execution
     */
    private <T> T evaluateServerJsScript(
            ServerJs serverJs,
            Map<String, Object> externalModel,
            List<String> externalArguments,
            Class<T> resultType) {

        if (serverJs == null) {
            error("[evaluateServerJsScript] ServerJs is null");
            throw new RuntimeException("No entry");
        }

        String script = serverJs.getCode();
        try {
            PageModelMap map = serverJs.getModelMap();
            if (externalModel != null) {
                map.putAll(externalModel);
            }
            if (CollectionUtils.isNotEmpty(externalArguments)) {
                map.put(arguments, externalArguments);
            }
            return evaluateScript(script, map, resultType, null);
        } catch (Exception e) {
            error(e, "[evaluateServerJsScript] When evaluating {}", serverJs.getName());
            throw new RuntimeException("Error when evaluating js", e);
        }
    }


    //TODO: this method is to be redesigned
    private <T> T evaluateScript(String script, Map<String, Object> bindings, Class<T> resultType, Writer log) {
        Context c = contextBuilder.build();
        Value b = c.getBindings("js");
        for (Map.Entry<String, Object> o : bindings.entrySet()) {
            b.putMember(o.getKey(), o.getValue());
        }
        for (Map.Entry<String, Object> o : ComponentProvider.resources.entrySet()) {
            b.putMember(o.getKey(), o.getValue());
        }
        b.putMember("model", bindings);
        b.putMember("process", new ServerJSProcessRunner(services, log == null ? new NullWriter() : log));
        T result = c.eval("js", script).as(resultType);
        return result;
    }

}
