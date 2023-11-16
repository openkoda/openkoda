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

import com.openkoda.controller.api.v1.model.RefreshTokenRequest;
import com.openkoda.controller.api.v1.model.RefresherTokenRequest;
import com.openkoda.controller.api.v1.model.TokenRequest;
import com.openkoda.controller.api.v1.model.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.openkoda.controller.common.URLConstants._API_AUTH;

@RestController
@RequestMapping(_API_AUTH)
public class TokenControllerApiV1 extends AbstractTokenControllerApiV1 {

    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    @PostMapping(_TOKEN + _USER + _ID )
    public ResponseEntity<TokenResponse> get(
            @PathVariable(ID) Long userId, @RequestBody TokenRequest aTokenRequest) {
        debug("[getToken] UserId: {}", userId);
        return (ResponseEntity<TokenResponse>)
               getToken(userId, aTokenRequest)
                        .mav(
                                a -> ResponseEntity.ok(a.get(tokenResponse)),
                                a -> new ResponseEntity(a.get(message), HttpStatus.NOT_FOUND)
                        );
    }
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    @PostMapping(_TOKEN + _REFRESH)
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest aTokenRequest) {
        debug("[getToken] UserId: {}");
        return (ResponseEntity<TokenResponse>)
                refreshToken(aTokenRequest)
                .mav(
                    a -> ResponseEntity.ok(a.get(tokenResponse)),
                    a -> new ResponseEntity(a.get(message), HttpStatus.NOT_FOUND)
                );
    }
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    @PostMapping(_TOKENREFRESHER + _USER )
    public ResponseEntity<TokenResponse> getRefresher(@RequestBody RefresherTokenRequest aRefresherTokenRequest) {
        String username = aRefresherTokenRequest.getLogin();
        debug("[getTokenRefresher] username: {}", username);
        return (ResponseEntity<TokenResponse>)
                getTokenRefresher(aRefresherTokenRequest, username)
                        .mav(
                                a -> ResponseEntity.ok(a.get(tokenResponse)),
                                a -> new ResponseEntity(a.get(message), HttpStatus.NOT_FOUND)
                        );
    }
}