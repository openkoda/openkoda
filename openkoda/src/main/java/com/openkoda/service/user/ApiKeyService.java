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

package com.openkoda.service.user;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.api.v1.model.TokenRequest;
import com.openkoda.controller.api.v1.model.TokenResponse;
import com.openkoda.model.Token;
import com.openkoda.model.User;
import com.openkoda.model.authentication.ApiKey;
import jakarta.inject.Inject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class ApiKeyService extends ComponentProvider {

    private static SecureRandom random = new SecureRandom();

    @Inject
    private PasswordEncoder passwordEncoder;

    public Tuple3<User, ApiKey, String> resetApiKey(User user) {
        debug("[resetApiKey] User: {}", user.getId());
        byte[] apiKeyBytes = new byte[30];
        random.nextBytes(apiKeyBytes);
        String plainApiKey = Base64.getUrlEncoder().encodeToString(apiKeyBytes);
        ApiKey apiKey = user.getApiKey();
        if (apiKey == null) {
            apiKey = new ApiKey(plainApiKey, user);
            apiKey.setId(user.getId());
            user.setApiKey(apiKey);
        } else {
            apiKey.setPlainApiKey(plainApiKey);
        }

        return Tuples.of(user, apiKey, plainApiKey);

    }

    public ApiKey verifyTokenRequest(TokenRequest getTokenRequest, User user) {
        debug("[verifyTokenRequest] User: {}", user.getId());
        if (user == null) {
            throw new RuntimeException("invalid user");
        }

        ApiKey apiKey = user.getApiKey();
        if (apiKey == null) {
            throw new RuntimeException("no api key");
        }

        if (not(passwordEncoder.matches(getTokenRequest.getApiKey(), apiKey.getApiKey()))) {
            throw new RuntimeException("incorrect apiKey");
        }

        return apiKey;

    }

    public TokenResponse createTokenResponse(Token token) {
        debug("[createTokenResponse]");
        Long userId = token.getUserId();
        String apiToken = token.getUserIdAndTokenBase64String();
        return new TokenResponse(apiToken, userId, token.getExpiresOn());
    }
}