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

package com.openkoda.core.tracker;

import com.openkoda.core.flow.mbean.StatisticsMBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-08-22
 */
public class DebugLogsDecoratorWithRequestId implements StatisticsMBean, LoggingComponentWithRequestId {

    public DebugLogsDecoratorWithRequestId() {
    }

    public boolean turnOnDebugModeForLoggerClassname(String classname) {
        this.debug("[turnOnDebugModeForLoggerClassname] {}", new Object[]{classname});
        if (StringUtils.isBlank(classname)) {
            return false;
        } else {
            Iterator var2 = loggers.entrySet().iterator();

            Map.Entry e;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                e = (Map.Entry)var2.next();
            } while(!classname.equals(((Class)e.getKey()).getName()));

            return this.turnOnDebugModeForLoggerClass((Class)e.getKey());
        }
    }

    public boolean turnOnDebugModeForLoggerClass(Class c) {
        this.debug("[turnOnDebugModeForLoggerClass] {}", new Object[]{c});
        return c == null ? false : debugLoggers.add(c);
    }

    public boolean turnOffDebugModeForLoggerClassname(String classname) {
        this.debug("[turnOffDebugModeForLoggerClassname] {}", new Object[]{classname});
        Iterator var2 = loggers.entrySet().iterator();

        Map.Entry e;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            e = (Map.Entry)var2.next();
        } while(!classname.equals(((Class)e.getKey()).getName()));

        return this.turnOffDebugModeForLoggerClass((Class)e.getKey());
    }

    public boolean turnOffDebugModeForLoggerClass(Class c) {
        return c == null ? false : debugLoggers.remove(c);
    }

    public void clearDebugLoggers() {
        debugLoggers.clear();
    }

    public String[] collectLoggerNames() {
        return (String[])availableLoggers.stream().map((a) -> {
            return a.getName();
        }).toArray((x$0) -> {
            return new String[x$0];
        });
    }

    public boolean setMaxEntries(int maxEntries) {
        debugStack.setMaxEntries(maxEntries);
        return true;
    }
}
