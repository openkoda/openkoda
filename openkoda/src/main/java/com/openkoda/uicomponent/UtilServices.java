package com.openkoda.uicomponent;

import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.model.file.File;
import com.openkoda.service.csv.CsvService;
import jakarta.inject.Inject;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public File toCSV(String filename, List<Object[]> data, String ... headers) throws IOException {
        return csvService.createCSV(filename, data, headers);
    }
}
