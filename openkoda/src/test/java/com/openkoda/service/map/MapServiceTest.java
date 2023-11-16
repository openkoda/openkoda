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

package com.openkoda.service.map;

import com.openkoda.AbstractTest;
import com.vividsolutions.jts.geom.Geometry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class MapServiceTest extends AbstractTest {

    @Test
    public void shouldParseInitialPoint() {

        //given
        String point = "POINT (0 0)";

        //when
        Geometry geometry = MapService.parsePoint(point);

        //then
        assertEquals("Point", geometry.getGeometryType());
        assertEquals(geometry.toText(), point);

    }

    @Test
    public void shouldParseCodedoseCords() {

        //given
        String point = "POINT (51.1083 17.0352)";

        //when
        Geometry geometry = MapService.parsePoint(point);

        //then
        assertEquals("Point", geometry.getGeometryType());
        assertEquals(geometry.toText(), point);

    }

    @Test
    public void shouldParseNegativeCodedoseCords_withPositiveLng() {

        //given
        String point = "POINT (-51.1083 17.0352)";

        //when
        Geometry geometry = MapService.parsePoint(point);

        //then
        assertEquals("Point", geometry.getGeometryType());
        assertEquals(geometry.toText(), point);

    }

    @Test
    public void shouldParseNegativeCodedoseCords_withPositiveLang() {

        //given
        String point = "POINT (51.1083 -17.0352)";

        //when
        Geometry geometry = MapService.parsePoint(point);

        //then
        assertEquals("Point", geometry.getGeometryType());
        assertEquals(geometry.toText(), point);

    }

    @Test
    public void shouldParseNegativeCodedoseCords() {

        //given
        String point = "POINT (51.1083 -17.0352)";

        //when
        Geometry geometry = MapService.parsePoint(point);

        //then
        assertEquals("Point", geometry.getGeometryType());
        assertEquals(geometry.toText(), point);

    }
    @Test
    public void shouldParseMaximumLatitudeAndLongitude() {

        //given
        String point = "POINT (180 90)";

        //when
        Geometry geometry = MapService.parsePoint(point);

        //then
        assertEquals("Point", geometry.getGeometryType());
        assertEquals(geometry.toText(), point);

    }
    @Test
    public void shouldParseMinimumLatitudeAndLongitude() {

        //given
        String point = "POINT (-180 -90)";

        //when
        Geometry geometry = MapService.parsePoint(point);

        //then
        assertEquals("Point", geometry.getGeometryType());
        assertEquals(geometry.toText(), point);

    }

    @Test
    public void shouldNotParseLongitudeOver180() {
        // given
        String point = "POINT (181 -17.0352)";

        // when
        Executable parsePointExecutable = () -> MapService.parsePoint(point);

        // then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, parsePointExecutable);
        String expectedMessage = "Longitude should between -180 and 180";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotParseLongitudeBelowMinus180() {
        // given
        String point = "POINT (-181 -17.0352)";

        // when
        Executable parsePointExecutable = () -> MapService.parsePoint(point);

        // then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, parsePointExecutable);
        String expectedMessage = "Longitude should between -180 and 180";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotParseLatitudeOver90() {
        // given
        String point = "POINT (11 97.0352)";

        // when
        Executable parsePointExecutable = () -> MapService.parsePoint(point);

        // then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, parsePointExecutable);
        String expectedMessage = "Latitude should between -180 and 180";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotParseLatitudeBelowMinus90() {
        // given
        String point = "POINT (11 -97.0352)";

        // when
        Executable parsePointExecutable = () -> MapService.parsePoint(point);

        // then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, parsePointExecutable);
        String expectedMessage = "Latitude should between -180 and 180";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotParseTextWithoutPoint() {
        // given
        String point = "(11 -97.0352)";

        // when
        Executable parsePointExecutable = () -> MapService.parsePoint(point);

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, parsePointExecutable);
        String expectedMessage = "com.vividsolutions.jts.io.ParseException: Unknown geometry type: ( (line 1)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    public void shouldNotParseTextWithMisspelledPoint() {
        // given
        String point = "P0INT (11 -97.0352)";
        String type = point.split(" ")[0];
        // when
        Executable parsePointExecutable = () -> MapService.parsePoint(point);

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, parsePointExecutable);
        String expectedMessage = "com.vividsolutions.jts.io.ParseException: Unknown geometry type: " + type + " (line 1)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotParseTextWithCommaBetweenPoints() {
        // given
        String point = "POINT (11, -97.0352)";

        // when
        Executable parsePointExecutable = () -> MapService.parsePoint(point);

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, parsePointExecutable);
        String expectedMessage = "com.vividsolutions.jts.io.ParseException: Expected number but found ',' (line 1)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotParseGeometryType() {
        // given
        String point = "GEOMETRY (11 -97.0352)";

        // when
        Executable parsePointExecutable = () -> MapService.parsePoint(point);

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, parsePointExecutable);
        String expectedMessage = "com.vividsolutions.jts.io.ParseException: Unknown geometry type: GEOMETRY (line 1)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}
