package com.openkoda.core.cache;

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import static com.openkoda.controller.common.URLConstants.EXTERNAL_SESSION_ID;

/**
 * Cache for objects within same Http Request and session scope. It's determined based on Http Session ID, timestamp of a request and requestURI
 *
 * @author borowa
 * @since 08-05-2024
 */
@Service
public class RequestSessionCacheService implements LoggingComponentWithRequestId {

    @Value("${cache.request.session.enabled:false}")
    private boolean requestSessionCacheEnabled;
    
    @SuppressWarnings("rawtypes")
    private final Map<Class, CacheObject> objectCache = new ConcurrentHashMap<>();
    
    class CacheObject<T> {
        Map<String, RequestSessionContextMetadata<T>> cacheMap = new ConcurrentHashMap<>();
        ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    }        
    
    public <T> T tryGet(Class<T> clazz, Supplier<T> producer) {
        if(!requestSessionCacheEnabled) {
            return producer.get();
        }
        
        @SuppressWarnings("unchecked")
        CacheObject<T> cache = objectCache.get(clazz);
        if(cache == null) {
            objectCache.put(clazz, cache = new CacheObject<T>());
        }
        
        Lock writeLock = cache.cacheLock.writeLock();
        writeLock.lock();
        T object = null;
        try {
            RequestSessionContextMetadata<T> requestSessionMeta = getRequestSessionMetadata();
            if(requestSessionMeta == null) {
                debug(">>> [preHandle] no request context, getting object");
                object = producer.get();
                return object;
            }
            
            RequestSessionContextMetadata<T> cachedRequestSessionMeta = cache.cacheMap.get(requestSessionMeta.getSessionId());
            if(cachedRequestSessionMeta != null && cachedRequestSessionMeta.getTimestamp() == requestSessionMeta.getTimestamp()) {
                debug("=== [preHandle] using cached object {}", clazz);
                object = cachedRequestSessionMeta.getCached();
            } else {
                debug(">>> [preHandle] getting object {}", clazz);
                object = producer.get();
                debug("<<<[preHandle] got object {}", clazz);            
                
                requestSessionMeta.setCached(object);
                cache.cacheMap.put(requestSessionMeta.getSessionId(), requestSessionMeta);
            }
        } finally {
            try {
                writeLock.unlock();
            }catch (Throwable th) {
                error("Could not unlock {}", th.getMessage());
            }
        }
        
        return object;
    }
    
    public <T> RequestSessionContextMetadata<T> getRequestSessionMetadata() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
            return getRequestSessionMetadata(request);
        } else {
            return null;
        }
    }
    
    public <T> RequestSessionContextMetadata<T> getRequestSessionMetadata(HttpServletRequest request) {
        final boolean isWidget = "TRUE".equalsIgnoreCase(request.getParameter("widget"));
        long timestamp = -1;
        String timestampString = request.getParameter("timestamp");
        if(StringUtils.isBlank(timestampString)) {
            timestampString = (String)request.getAttribute("rtimestamp");
        }
        
        if(StringUtils.isBlank(timestampString)) {
            RequestContextHolder.getRequestAttributes().setAttribute("rtimestamp", String.valueOf(System.currentTimeMillis()), 0);
            timestampString = (String)request.getAttribute("rtimestamp");
        }
        
        if(StringUtils.isNotBlank(timestampString)) { 
            timestamp = Long.parseLong(timestampString.trim());
        }
        
        final String sessionId = request.getSession().getId();
        String externalSession = request.getParameter(EXTERNAL_SESSION_ID);
        return new RequestSessionContextMetadata<>(sessionId, externalSession, timestamp, isWidget, request.getRequestURI());
    }
}
