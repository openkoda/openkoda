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

package com.openkoda.service.user;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.model.Privilege;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.Token;
import com.openkoda.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;

import java.util.*;

/**
 * <p>Token Service</p>
 * The tokens can be user to authenticate request (either as GET request token parameter or HTTP Header in API call)
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2018-12-14
 *
 */
@Service
public class TokenService extends ComponentProvider {
    private static final Set<Enum> REFRESHER_PRIVILEGE_SET = Collections.singleton(Privilege.canRefreshTokens);

    @Value("${tokens.refresher.expiration:2592000}")
    int refresherTokenExpiration;

    /**
     * Creates multiple use token for the given userId.
     */
    public Token createMultipleUseTokenForUser(Long id, int expirationTimeInSeconds, PrivilegeBase ... allowedPrivileges) {
        debug("[createMultipleUseTokenForUser] {}", id);
        return repositories.unsecure.token.saveAndFlush(new Token(repositories.unsecure.user.findOne(id), true, false, expirationTimeInSeconds, allowedPrivileges));
    }

    /**
     * Creates multiple use token for the given userId.
     */
    public Token createMultipleUseTokenForUser(Long id, PrivilegeBase ... allowedPrivileges) {
        debug("[createMultipleUseTokenForUser] {}", id);
        return repositories.unsecure.token.saveAndFlush(new Token(repositories.unsecure.user.findOne(id), false, allowedPrivileges));
    }

    /**
     * Creates token for the given user.
     */
    public Token createTokenForUser(User user) {
        debug("[createTokenForUser] {}", user.getId());
        return repositories.unsecure.token.saveAndFlush(new Token(user));
    }

    /**
     * Creates token for the given user with narrowed privileges.
     * Once the request is authenticated, the user should have narrowed privileges only to these in the token.
     */
    public Token createTokenForUser(User user, PrivilegeBase... allowedPrivileges) {
        debug("[createTokenForUser] {} with privileges {}", user.getId(), Arrays.toString(allowedPrivileges));
        return repositories.unsecure.token.saveAndFlush(new Token(user, allowedPrivileges));
    }

    /**
     * Creates token for the given user with narrowed privileges and expiration time.
     * Once the request is authenticated, the user should have narrowed privileges only to these in the token.
     */
    public Token createTokenForUser(User user, int expirationTimeInSeconds, PrivilegeBase ... allowedPrivileges) {
        debug("[createTokenForUser] {} with privileges {}", user.getId(), Arrays.toString(allowedPrivileges));
        return repositories.unsecure.token.saveAndFlush(new Token(user, expirationTimeInSeconds, allowedPrivileges));
    }

    /**
     * This is useful method for tokens that are valid only for one use.
     */
    public Token verifyAndInvalidateToken(String base64UserIdToken) {
        debug("[verifyAndInvalidateToken]");
        Tuple2<Token, String> token = repositories.unsecure.token.findByBase64UserIdTokenIsValidTrue(base64UserIdToken);
        if(token.getT1() != null) {
            debug("[verifyAndInvalidateToken] invalidating token {}", token.getT1().getId());
            repositories.unsecure.token.saveAndFlush(token.getT1().invalidate());
            return token.getT1();
        }
        debug("[verifyAndInvalidateToken] {}", token.getT2());
        return null;
    }

    /**
     * Method to obtain refresher token that can be used to retrieve short living tokens.
     * @param user obtaining refresher token
     * @return refresher token, raises NullPointerException when supplied user is null
     */
    public Token createRefresherTokenForUser(User user) {
        user = Objects.requireNonNull(user);
        debug("[createRefresherTokenForUser] for user {}", user.getId());
        var token = new Token(user, refresherTokenExpiration, Privilege.canRefreshTokens);
        return repositories.unsecure.token.saveAndFlush(token);
    }

    /** Method to obtain token from refresher, validating if refresher token only privilege (!) is to refresh user tokens
     *
     * Method rejects the refresher token if it has other privileges than Privilege.canRefreshTokens, just to confuse the russians.
     *
     * @param refresherTokenBase64 base64 representation of refresher token
     * @return newly generated default user token
     */
    public Token createTokenForRefresher(String refresherTokenBase64) {
        debug("[createTokenForRefresher]");
        refresherTokenBase64 = Objects.requireNonNull(refresherTokenBase64);
        Tuple2<Token, String> tokenTuple = repositories.unsecure.token.findByBase64UserIdTokenIsValidTrue(refresherTokenBase64);
        Optional<Token> tokenRefresher = Optional.ofNullable(tokenTuple.getT1());
        return tokenRefresher.filter(token -> token.getPrivilegesSet().equals(REFRESHER_PRIVILEGE_SET)).map(token -> {
            var user = tokenRefresher.get().getUser();
            var tokenToReturn = new Token(user);
            repositories.unsecure.token.saveAndFlush(tokenToReturn);
            return tokenToReturn;
        }).orElseGet(() -> {
            debug("[createTokenForRefresher] refresher token invalid: {}", tokenTuple.getT2());
            return null;
        });
    }
}
