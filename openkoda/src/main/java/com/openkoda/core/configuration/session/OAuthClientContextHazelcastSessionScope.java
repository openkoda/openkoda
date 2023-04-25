///*
//MIT License
//
//Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>
//
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
//documentation files (the "Software"), to deal in the Software without restriction, including without limitation
//the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
//and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice
//shall be included in all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
//INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
//OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
//IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//*/
//
//package com.openkoda.core.configuration.session;
//
//import com.openkoda.core.helper.ApplicationContextProvider;
//import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
//import org.springframework.security.oauth2.client.token.AccessTokenRequest;
//
///**
// * Specialized Hazelcast Session Scope for providing DefaultOAuth2ClientContext Beans.
// * This scope was introduced to workaround this issue:
// * https://github.com/hazelcast/hazelcast/issues/3746
// *
// * Long story short: beans with @Scope(value = "session") don't work nicely with hazelcast session replication.
// * (they are not propagated by spring)
// *
// * This scope allows to propagate DefaultOAuth2ClientContext beans (which have to be session beans) in both
// * standalone application and hazelcast deployment with session replication.
// */
//public class OAuthClientContextHazelcastSessionScope extends AbstractHazelcastSessionScope<DefaultOAuth2ClientContext> {
//
//    /**
//     * Name of the custom Spring bean scope
//     */
//    public static final String SESSION_HAZELCAST_AWARE = "sessionHazelcastAware";
//
//    private DefaultOAuth2ClientContext createContext() {
//        AccessTokenRequest r = ApplicationContextProvider.getContext().getBean("accessTokenRequest", AccessTokenRequest.class);
//        DefaultOAuth2ClientContext context = new DefaultOAuth2ClientContext(r);
//        return context;
//    }
//
//    public OAuthClientContextHazelcastSessionScope() {
//        super("OAuthClientContext", 60);
//        setCustomObjectFactory(this::createContext);
//    }
//}
