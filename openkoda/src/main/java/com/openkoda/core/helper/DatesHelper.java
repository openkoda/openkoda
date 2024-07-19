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

package com.openkoda.core.helper;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * <p>Dates helper provides a set of static methods for operations on dates.</p>
 * This class uses hard-coded English locale.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
@Component("dates")
public class DatesHelper {

    public static String formatDateTimeLocaleEN(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public static String formatDateLocaleEN(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public static String formatDateTimeLocaleEN(LocalDateTime date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);
        return date.format(dateFormat);
    }

    public static String formatDateTimeEN(LocalDateTime date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        return date.format(dateFormat);
    }

    public static String formatDateWithFullMonthNameLocaleEN(LocalDateTime date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
        return date.format(dateFormat);
    }

    public static String formatDateTimeWithFullMonthName(LocalDateTime date, String languageTag) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag(languageTag));
        return date.format(dateFormat);
    }

    public static String formatDateWithFullMonthName(LocalDateTime date, String languageTag) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag(languageTag));
        return date.format(dateFormat);
    }

    public static String formatDateLocaleEN(LocalDateTime date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return date.format(dateFormat);
    }

    public static LocalDateTime getDatePlusMonthsFromCurrent(long months) {
        return LocalDateTime.now().plusMonths(months);
    }

    public static LocalDateTime getDatePlusDaysFromCurrent(long days) {
        return LocalDateTime.now().plusDays(days);
    }

    public static LocalDateTime secondsToLocalDateTime(long seconds) {
        return Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
