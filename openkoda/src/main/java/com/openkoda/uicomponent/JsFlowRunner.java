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

package com.openkoda.uicomponent;

import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.flow.form.JsFlow;
import com.openkoda.core.flow.form.JsResultAndModel;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.repository.SecureServerJsRepository;
import com.openkoda.service.map.MapService;
import com.openkoda.uicomponent.live.LiveComponentProvider;
import com.vividsolutions.jts.geom.Point;
import jakarta.inject.Inject;
import org.graalvm.polyglot.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.openkoda.controller.common.PageAttributes.*;

@Component
public class JsFlowRunner {

    private Context.Builder contextBuilder = Context.newBuilder("js")
            .allowHostAccess(HostAccess.ALL)
            .allowAllAccess(true)
            .allowHostClassLoading(true)
            .allowHostClassLookup(className -> true);

    @Inject
    private LiveComponentProvider componentProvider;

    @Autowired(required = false)
    private PreviewComponentProviderInterface previewComponentProviderInterface;

    @Autowired
    private SecureServerJsRepository serverJsRepository;
    @Autowired
    private JsParser jsParser;
    @Inject
    private FileSystemImpl fileSystemImp;

    private Flow evaluateJsFlow(String jsFlow, Flow initializedFlow, JsResultAndModel initialResultAndModel, String scriptSourceFileName) {
        Context c = Context.newBuilder("js")
                .fileSystem(fileSystemImp)
                .allowIO(true)
                .allowHostAccess(HostAccess.ALL)
                .allowAllAccess(true)
                .allowHostClassLoading(true)
                .allowHostClassLookup(className -> true)
                .build();
        Value b = c.getBindings("js");
        b.putMember("flow", initializedFlow);
        b.putMember("context", initialResultAndModel);
        b.putMember("dateNow", (Supplier<LocalDate>) () -> componentProvider.util.dateNow());
        b.putMember("dateTimeNow", (Supplier<LocalDateTime>) () -> componentProvider.util.dateTimeNow());
        b.putMember("parseInt", (Function<String, Integer>) s -> componentProvider.util.parseInt(s));
        b.putMember("parseLong", (Function<String, Long>) s -> componentProvider.util.parseLong(s));
        b.putMember("parseDate", (Function<String, LocalDate>) s -> componentProvider.util.parseDate(s));
        b.putMember("parseTime", (Function<String, LocalTime>) s -> componentProvider.util.parseTime(s));
        b.putMember("toString", (Function<Object, String>) s -> componentProvider.util.toString(s));
        b.putMember("isNaN", (Function<Double, Boolean>) s -> componentProvider.util.isNaN(s));
        b.putMember("parseFloat", (Function<String, Float>) s -> componentProvider.util.parseFloat(s));
        b.putMember("parseJSON", (Function<String, JSONObject>) s -> componentProvider.util.parseJSON(s));
        b.putMember("parsePoint", (Function<String, Point>) s -> MapService.parsePoint(s));
        b.putMember("toJSON", (Function<Object, String>) s -> componentProvider.util.toJSON(s));
        b.putMember("decodeURI", (Function<String, String>) s -> componentProvider.util.decodeURI(s));
        b.putMember("encodeURI", (Function<String, String>) s -> componentProvider.util.encodeURI(s));

        String finalScript = jsFlow.replaceFirst("flow", "let result = flow");
        finalScript +=   ";\nresult";
        try {
            return c.eval(Source.newBuilder("js", finalScript, scriptSourceFileName).build()).as(initializedFlow.getClass());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private PageModelMap executeFlow(Flow f, PageModelMap initialModel) {
        return f.execute(initialModel);
    }

    public PageModelMap runPreviewFlow(String jsFlow, Map<String, String> params, Long organizationId, long userId, AbstractOrganizationRelatedEntityForm form, String scriptSourceFileName) {
        Flow f = JsFlow.init(previewComponentProviderInterface, params, form)
                .thenSet(organizationEntityId, userEntityId, a -> Tuples.of(organizationId, userId));

        try {
            JsResultAndModel initialResultAndModel = JsResultAndModel.constructNew(componentProvider, params, form);
            return executeFlow(evaluateJsFlow(jsFlow, f, initialResultAndModel, scriptSourceFileName), initialResultAndModel.model);
        } catch (PolyglotException e) {
            PageModelMap pageModelMap = new PageModelMap();
            pageModelMap.put(errorMessage, e.getMessage());
            return pageModelMap;
        }
    }

    public PageModelMap runLiveFlow(String jsFlow, Map<String, String> params, Long organizationId, long userId, AbstractOrganizationRelatedEntityForm form, String scriptSourceFileName) {
        Flow f = JsFlow.init(componentProvider, params, form)
                .thenSet(organizationEntityId, userEntityId, a -> Tuples.of(organizationId, userId));

        JsResultAndModel initialResultAndModel = JsResultAndModel.constructNew(componentProvider, params, form);
        return executeFlow(evaluateJsFlow(jsFlow, f, initialResultAndModel, scriptSourceFileName), initialResultAndModel.model);
    }
}
