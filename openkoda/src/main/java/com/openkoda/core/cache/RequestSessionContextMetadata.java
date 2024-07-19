package com.openkoda.core.cache;

import java.util.Objects;

/**
 * Contains set of request scope related meta information
 *
 * @author borowa
 * @since 10-05-2024
 * @param <T>
 */
public class RequestSessionContextMetadata<T> {
    private final String sessionId;
    private final String externalSessionId;
    private final long timestamp;
    
    // used in case given request is fired by a 'widget' beeing a part of a whole dashboard or set of other independend requests fired in parallel
    private boolean isWidget;
    private final String requestURI;
    private T cached;
    
    public RequestSessionContextMetadata(String sessionId, String externalSessionId, long timestamp,
            boolean isWidget, String requestURI) {
        super();
        this.sessionId = sessionId;
        this.externalSessionId = externalSessionId;
        this.timestamp = timestamp;
        this.isWidget = isWidget;
        this.requestURI = requestURI;
    }
    
    public String getSessionId() {
        return sessionId;
    }

    public String getExternalSessionId() {
        return externalSessionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isWidget() {
        return isWidget;
    }
    
    public void setWidget(boolean isWidget) {
        this.isWidget = isWidget;
    }
    
    public String getRequestURI() {
        return requestURI;
    }

    public T getCached() {
        return cached;
    }
    
    public void setCached(T cached) {
        this.cached = cached;
    }
    
    @Override
    public boolean equals(Object obj) {     
        @SuppressWarnings("unchecked")
        RequestSessionContextMetadata<T> objCasted = (RequestSessionContextMetadata<T>)obj;
        return objCasted != null && this.isWidget == objCasted.isWidget && this.timestamp == objCasted.timestamp && this.sessionId.equals(objCasted.sessionId) 
                && this.externalSessionId.equals(objCasted.externalSessionId) && this.requestURI.equals(objCasted.requestURI);
    }
    
    @Override
    public int hashCode() {            
        return Objects.hash(sessionId, externalSessionId, timestamp, isWidget, requestURI);
    }
}