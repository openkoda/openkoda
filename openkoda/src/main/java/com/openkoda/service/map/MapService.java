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

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.springframework.stereotype.Service;

@Service
public class MapService {
    public static final String DEFAULT_POINT = "POINT (0 0)";

    private static final WKTReader wtkReader = new WKTReader();

    public static Point parsePoint(String s) {
        try {
            Point point = (Point) wtkReader.read(s);
            if(!(point.getX() <= 180.0) || !(point.getX() >= -180.0)){
                throw new IllegalArgumentException("Longitude should between -180 and 180");
            }
            else if (!(point.getY() <= 90.0) || !(point.getY() >= -90.0)){
                throw new IllegalArgumentException("Latitude should between -180 and 180");
            }
            return point;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
