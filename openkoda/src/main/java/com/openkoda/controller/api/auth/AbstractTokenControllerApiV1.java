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
