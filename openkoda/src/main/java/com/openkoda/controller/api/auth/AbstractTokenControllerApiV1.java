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

package com.openkoda.controller.api.auth;

import com.openkoda.controller.api.ApiAttributes;
import com.openkoda.controller.api.v1.model.RefreshTokenRequest;
import com.openkoda.controller.api.v1.model.RefresherTokenRequest;
import com.openkoda.controller.api.v1.model.TokenRequest;
import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;

public class AbstractTokenControllerApiV1 extends AbstractController implements ApiAttributes {

    protected PageModelMap getToken(Long userId, TokenRequest aTokenRequest){
        return Flow.init(userEntity, repositories.unsecure.user.findOne(userId))
                .then(a -> services.apiKey.verifyTokenRequest(aTokenRequest, a.result))
                .then(a -> services.token.createTokenForUser(a.result.getUser()))
                .thenSet(tokenResponse, a -> services.apiKey.createTokenResponse(a.result))
                .execute();
    }

    protected PageModelMap refreshToken(RefreshTokenRequest aTokenRequest){
        return Flow.init(null, services.token.createTokenForRefresher(aTokenRequest.getTokenRefresher()))
                .thenSet(tokenResponse, a -> services.apiKey.createTokenResponse(a.result))
                .execute();
    }

    protected PageModelMap getTokenRefresher(RefresherTokenRequest aRefresherTokenRequest, String username){
        return Flow.init(userEntity, repositories.unsecure.user.findByLogin(username))
                .then(a -> services.user.verifyPassword(a.result, aRefresherTokenRequest.getPassword()))
                .then(a -> services.token.createRefresherTokenForUser(a.result))
                .thenSet(tokenResponse, a -> services.apiKey.createTokenResponse(a.result))
                .execute();
    }
}
