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

package com.openkoda.core.configuration.session;

import com.openkoda.core.helper.ClusterHelper;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.SessionScope;

import java.util.concurrent.TimeUnit;


/**
 * Custom abstract Spring scope for building Hazelcast aware session beans.
 * The general contract is following:
 * If the application works in cluster (ie. ClusterHelper.isCluster() == true),
 * then add the bean to the replicated Hazelcast cache.
 * If the application is standalone (ie. ClusterHelper.isCluster() == false),
 * then use standard http session instead.
 *
 * Optionally a custom bean factory method (objectFactory) can be provided for creation of specialized beans.
 * Optionally a eviction time in seconds for hazelcast entries can be specified.
 * @param <T>
 */
public abstract class AbstractHazelcastSessionScope<T> extends SessionScope {

    /**
     * Name of the Hazelcast cache storing sessions
     */
    public static final String CACHE_NAME = "HazelcastScope";

    /**
     * Custom ObjectFactory
     */
    private ObjectFactory<T> customObjectFactory;

    /**
     * key prefix for cache entries
     */
    private final String keyPrefix;

    /**
     * TTL for hazelcast entries (0 == no eviction)
     */
    private final long entryTTLInSeconds;

    /**
     * @param keyPrefix - prefix for keys in hazelcast cache
     * @param entryTTLInSeconds - optional entry eviction time in seconds (0 == no eviction)
     */
    public AbstractHazelcastSessionScope(String keyPrefix, long entryTTLInSeconds) {
        this.keyPrefix = keyPrefix;
        this.entryTTLInSeconds = entryTTLInSeconds;
    }

    @Override
    public Object get(String s, ObjectFactory<?> objectFactory) {

        //if not cluster, use standard session scope
        if (!ClusterHelper.isCluster()) {
            Object result = super.get(s, objectFactory);
            return result;
        }

        String key = getEntryKey(s);
        Object existingEntry = ClusterHelper.getHazelcastInstance().getReplicatedMap(CACHE_NAME).get(key);

        //if entry already exists, return
        if (existingEntry != null) {
            return existingEntry;
        }

        //otherwise, create new entry and insert it into hazelcast
        Object newEntry = customObjectFactory == null ? objectFactory.getObject() : customObjectFactory.getObject();
        if (entryTTLInSeconds > 0) {
            ClusterHelper.getHazelcastInstance().getReplicatedMap(CACHE_NAME).put(key, newEntry, entryTTLInSeconds, TimeUnit.SECONDS);
        } else {
            ClusterHelper.getHazelcastInstance().getReplicatedMap(CACHE_NAME).put(key, newEntry);
        }
        return newEntry;
    }

    private String getEntryKey(String s) {
        return keyPrefix + s + RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    @Override
    public Object remove(String s) {
        //if not cluster, use standard session scope
        if (!ClusterHelper.isCluster()) {
            return super.remove(s);
        }
        String key = getEntryKey(s);
        Object result = ClusterHelper.getHazelcastInstance().getReplicatedMap(CACHE_NAME).remove(key);

        return result;
    }

    @Override
    public void registerDestructionCallback(String s, Runnable runnable) {
        //if not cluster, use standard session scope
        if (!ClusterHelper.isCluster()) {
            super.registerDestructionCallback(s, runnable);
        }
    }

    @Override
    public Object resolveContextualObject(String s) {
        //if not cluster, use standard session scope
        if (!ClusterHelper.isCluster()) {
            return super.resolveContextualObject(s);
        }
        return null;
    }

    protected ObjectFactory<T> getCustomObjectFactory() {
        return customObjectFactory;
    }

    protected void setCustomObjectFactory(ObjectFactory<T> customObjectFactory) {
        this.customObjectFactory = customObjectFactory;
    }

}