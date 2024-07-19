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
import com.openkoda.uicomponent.annotation.Autocomplete;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
public class UtilServices implements LoggingComponent {

    @Inject
    CsvService csvService;

    @Autocomplete(doc="Get the current date")
    public LocalDate dateNow() {
        return LocalDate.now();
    }
    @Autocomplete(doc="Get the current date and time")
    public LocalDateTime dateTimeNow() {
        return LocalDateTime.now();
    }
    @Autocomplete(doc="Parse string to integer")
    public int parseInt(String s) {
        return Integer.parseInt(s);
    }
    @Autocomplete(doc="Parse string to long")
    public long parseLong(String s) {
        return Long.parseLong(s);
    }
    @Autocomplete(doc="Parse date string to date object")
    public LocalDate parseDate(String s) {
        return LocalDate.parse(s);
    }
    @Autocomplete(doc="Parse time string (eg. 23:12) to time object")
    public LocalTime parseTime(String s) {
        return LocalTime.parse(s);
    }
    @Autocomplete(doc="Get string value of the object")
    public String toString(Object o) {
        return o.toString();
    }
    @Autocomplete(doc="Check for NaN (Not a Number)")
    public boolean isNaN(double d) {
        return Double.isNaN(d);
    }
    @Autocomplete(doc="Parse string to floating-point number")
    public float parseFloat(String s) {
        return Float.parseFloat(s);
    }
    @Autocomplete(doc="Parse JSON string to object")
    public JSONObject parseJSON(String s) {
        try {
            return new JSONObject(s);
        } catch (JSONException e) {
            error("JSON parsing failed because of: " + e.getMessage());
            return null;
        }
    }
    @Autocomplete(doc="Convert objects to JSON string")
    public String toJSON(Object o) {
        try {
            return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(o);
        } catch (IOException e) {
            error("JSON serialization failed because of: " + e.getMessage());
            return null;
        }
    }
    @Autocomplete(doc="Decode Uniform Resource Identifier (URI) string")
    public String decodeURI(String uri) {
        try {
            return URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return "Decoding failed because of: " + e.getMessage();
        }
    }
    @Autocomplete(doc="Encode Uniform Resource Identifier (URI) string")
    public String encodeURI(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return "Encoding failed because of: " + e.getMessage();
        }
    }
    @Autocomplete(doc="Export data to CSV file")
    public File toCSV(String filename, List<Object[]> data, String... headers) throws IOException, SQLException {
        return csvService.createCSV(filename, data.stream().map(Arrays::asList)
                .toList(), headers);
    }
    @Autocomplete(doc="Compute MD5 hash of string")
    public String md5(String value) {
        return DigestUtils.md5Hex(value);
    }
}
