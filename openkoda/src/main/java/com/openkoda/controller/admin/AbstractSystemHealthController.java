package com.openkoda.controller.admin;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.customisation.ServerJSProcessRunner;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;

public class AbstractSystemHealthController extends AbstractController {

    protected PageModelMap getSystemHealth(){
        return Flow.init()
                .thenSet(systemHealthStatus, a -> services.systemStatus.statusNow())
                .execute();
    }

    protected PageModelMap getThreads(){
        return Flow.init()
               .thenSet(serverJsThreads,  a ->ServerJSProcessRunner.getServerJsThreads())
                .execute();
    }

    protected PageModelMap interruptThread(Long threadId){
        return Flow.init()
                .then(a -> ServerJSProcessRunner.interruptThread(threadId))
                .execute();
    }

    protected PageModelMap removeThread(Long threadId){
        return Flow.init()
                .then(a -> ServerJSProcessRunner.removeJsThread(threadId))
                .execute();
    }
}
