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

package com.openkoda.repository.user;

import com.openkoda.core.flow.Tuple;
import com.openkoda.core.repository.common.FunctionalRepositoryWithLongId;
import com.openkoda.model.Token;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.Optional;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Repository
public interface TokenRepository extends FunctionalRepositoryWithLongId<Token> {

    Optional<Token> findByTokenAndIsValidTrue(String token);

    @Query("select new com.openkoda.core.flow.Tuple(t, t.used, t.expiresOn < CURRENT_TIMESTAMP) FROM " +
            "Token t WHERE (t.userId = :userId AND t.token = :token)")
    Tuple findByUserIdAndTokenWithInvalidationReasons(@Param("userId") Long userId, @Param("token") String token);
    Optional<Token> findByUserIdAndToken(Long userId, String token);

    String INVALID = "Invalid token";
    String ALREADY_USED = "This link has expired.";
    String EXPIRED = "This link has expired. You may contact your team members to get a new link.";

    //TODO Rule 3.3: Repository must not have business logic code.
    default @NotNull Tuple2<Token, String /* error message */> findByBase64UserIdTokenIsValidTrue(String base64UserIdToken) {
        boolean isBasicHeader = base64UserIdToken != null;
        if (!isBasicHeader) { return Tuples.of(null, INVALID); }
        String base64 = base64UserIdToken.trim();
        boolean isBase64 = Base64.isBase64(base64);
        if (!isBase64) { return Tuples.of(null, INVALID); }
        String[] idAndToken = new String(Base64.decodeBase64(base64)).split(":", 2);
        boolean isIdAndToken = idAndToken.length == 2;
        if (!isIdAndToken) { return Tuples.of(null, INVALID); }
        String idString = idAndToken[0];
        String tokenString = idAndToken[1];
        Long id;
        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            //id is not a number;
            return Tuples.of(null, INVALID);
        }
        Tuple t = this.findByUserIdAndTokenWithInvalidationReasons(id, tokenString);
        if (t == null) {
            return Tuples.of(null, INVALID);
        }

        Tuple3<Token, Boolean, Boolean> tokenWithInvalidationReasons = t.getT3();
        if (tokenWithInvalidationReasons.getT2()) {
            return Tuples.of(null, ALREADY_USED);
        }
        if (tokenWithInvalidationReasons.getT3()) {
            return Tuples.of(null, EXPIRED);
        }
        return Tuples.of(tokenWithInvalidationReasons.getT1(), "");
    }


}
