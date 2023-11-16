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

package com.openkoda.service.csv;

import com.openkoda.core.service.FileService;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvService implements LoggingComponentWithRequestId {

    public com.openkoda.model.file.File createCSV(String filename, List<Object[]> data, String... headers) throws IOException, SQLException {
        debug("[createCSV]");
        com.openkoda.model.file.File csvFile = new com.openkoda.model.file.File(
                StringUtils.endsWith(filename, ".csv") ? filename : filename + ".csv",
                "text/csv",
                FileService.StorageType.database
        );
        csvFile.setContent(new SerialBlob(createCSVByte(data, headers)));
        return csvFile;
    }

    private byte[] createCSVByte(List<Object[]> data, String... headers) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            data.add(0, headers);
            String csvContent = data.stream()
                    .map(this::convertToCSVRow)
                    .collect(Collectors.joining("\n"));
            outputStream.write(csvContent.getBytes());

            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String convertToCSVRow(Object[] rowData) {
        StringBuilder csvRow = new StringBuilder();
        for (int i = 0; i < rowData.length; i++) {
            if (i > 0) {
                csvRow.append(",");
            }
            csvRow.append(rowData[i]);
        }
        return csvRow.toString();
    }
}
