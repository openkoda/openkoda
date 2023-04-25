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

package com.openkoda.core.service;

import com.openkoda.model.task.HttpRequestTask;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Sends messages to Slack using webHooks. Collaborates with Thymeleaf in message generation(via FrontendResource).
 */
@Service
public class RestClientService {

    private RestTemplate restTemplate = new RestTemplate();

    public static HttpHeaders prepareHttpHeaders(HttpRequestTask task) {
        return prepareHttpHeaders(task.getHeadersMap());
    }

    public static HttpHeaders prepareHttpHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            httpHeaders.set(e.getKey(), e.getValue());
        }
        return httpHeaders;
    }

    public Map post(String url, Map<String, String> body, Map<String, String> headers) {
        HttpHeaders httpHeaders = prepareHttpHeaders(headers);
        HttpEntity<Map> httpEntity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, httpEntity, Map.class);
        return response.getBody();
    }

    public Map get(String url, Map<String, String> headers) {
        return get(url, headers, Map.class);
    }

    public <T> T get(String url, Map<String, String> headers, Class<T> resultType) {
        HttpHeaders httpHeaders = prepareHttpHeaders(headers);
        HttpEntity<Map> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, resultType);
        return response.getBody();
    }


}
