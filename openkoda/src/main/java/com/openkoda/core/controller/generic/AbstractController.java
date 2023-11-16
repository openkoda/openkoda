/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice 
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.controller.generic;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.DefaultComponentProvider;
import com.openkoda.controller.common.URLConstants;
import jakarta.inject.Inject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * <p>AbstractController class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class AbstractController extends ComponentProvider implements URLConstants {

    @Inject
    protected DefaultComponentProvider componentProvider;
    /**
     * <p>createPageable.</p>
     *
     * @param page a int.
     * @param size a int.
     * @param direction a {@link org.springframework.data.domain.Sort.Direction} object.
     * @param property a {@link java.lang.String} object.
     * @return a {@link org.springframework.data.domain.Pageable} object.
     */
    protected static Pageable createPageable(int page, int size, Sort.Direction direction, String property) {
        return PageRequest.of(page, size, direction, property);
    }

}
