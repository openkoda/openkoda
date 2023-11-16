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

package com.openkoda.uicomponent;

import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.model.file.File;
import com.openkoda.service.csv.CsvService;
import jakarta.inject.Inject;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class UtilServices implements LoggingComponent {

    @Inject
    CsvService csvService;

    public LocalDate dateNow() {
        return LocalDate.now();
    }

    public LocalDateTime dateTimeNow() {
        return LocalDateTime.now();
    }

    public int parseInt(String s) {
        return Integer.parseInt(s);
    }

    public long parseLong(String s) {
        return Long.parseLong(s);
    }

    public LocalDate parseDate(String s) {
        return LocalDate.parse(s);
    }

    public String toString(Object o) {
        return o.toString();
    }

    public boolean isNaN(double d) {
        return Double.isNaN(d);
    }

    public float parseFloat(String s) {
        return Float.parseFloat(s);
    }

    public JSONObject parseJSON(String s) {
        try {
            return new JSONObject(s);
        } catch (JSONException e) {
            error("JSON parsing failed because of: " + e.getMessage());
            return null;
        }
    }

    public String toJSON(Object o) {
        try {
            return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(o);
        } catch (IOException e) {
            error("JSON serialization failed because of: " + e.getMessage());
            return null;
        }
    }

    public String decodeURI(String uri) {
        try {
            return URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return "Decoding failed because of: " + e.getMessage();
        }
    }

    public String encodeURI(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return "Encoding failed because of: " + e.getMessage();
        }
    }

    public File toCSV(String filename, List<Object[]> data, String... headers) throws IOException, SQLException {
        return csvService.createCSV(filename, data, headers);
    }

    public String md5(String value) {
        return DigestUtils.md5Hex(value);
    }
}
