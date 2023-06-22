package com.openkoda.service.csv;

import com.openkoda.core.service.FileService;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class CsvService implements LoggingComponentWithRequestId {

    public com.openkoda.model.file.File createCSV(String filename, List<Object[]> data, String ... headers) throws IOException {
        debug("[createCSV]");

        File csv = new File(StringUtils.endsWith(filename, ".csv") ? filename : filename + ".csv");
        FileWriter out = new FileWriter(csv);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(headers)
                .build();

        try (final CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
            data.forEach((object) -> {
                try {
                    printer.printRecord(object);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
        return new com.openkoda.model.file.File(
                null,
                csv.getName(),
                "text/csv",
                csv.getTotalSpace(),
                null,
                FileService.StorageType.filesystem,
                csv.getPath()) ;
    }
}
