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

package reactor.util.function;


import com.openkoda.core.flow.PageModelFunction;

import java.util.Optional;

/**
 * 
 * Type alias by extension
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class ViewVariants extends Tuple4<Optional<String>, Optional<String>, Optional<PageModelFunction>, Optional<PageModelFunction>> {
    ViewVariants(Optional<String> s, Optional<String> s2, Optional<PageModelFunction> pageModelFunction, Optional<PageModelFunction> pageModelFunction2) {
        super(s, s2, pageModelFunction, pageModelFunction2);
    }

    /**
     * <p>of.</p>
     *
     * @param t1 a {@link java.util.Optional} object.
     * @param t2 a {@link java.util.Optional} object.
     * @param t3 a {@link java.util.Optional} object.
     * @param t4 a {@link java.util.Optional} object.
     * @return a {@link reactor.util.function.ViewVariants} object.
     */
    public static ViewVariants of(Optional<String> t1, Optional<String> t2, Optional<PageModelFunction> t3, Optional<PageModelFunction> t4) {
        return new ViewVariants(t1, t2, t3, t4);
    }

}
