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

package com.openkoda.core.service;

import com.openkoda.core.tracker.DebugLogsDecoratorWithRequestId;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used as a proxy to DebugLogsDecorator. Enables turning on and off loggers for 'collection' of classes.
 */
@Service
public class LogConfigService implements LoggingComponentWithRequestId {

    @Inject
    private DebugLogsDecoratorWithRequestId debugLogsDecorator;

    public int getMaxEntries() {
        debug("[getMaxEntries]");
        return debugLogsDecorator.debugStack.getMaxEntries();
    }

    public boolean setMaxEntries(int maxBuffer) {
        debug("[setMaxEntries] {}", maxBuffer);
        return debugLogsDecorator.setMaxEntries(maxBuffer);
    }

    public boolean turnOnDebugModeForLoggerClassname(String classname) {
        debug("[turnOnDebugModeForLoggerClassname] {}", classname);
        return debugLogsDecorator.turnOnDebugModeForLoggerClassname(classname);
    }

    public boolean turnOnDebugModeForLoggerClass(Class c) {
        debug("[turnOnDebugModeForLoggerClass] {}", c);
        return debugLogsDecorator.turnOnDebugModeForLoggerClass(c);
    }

    public boolean turnOffDebugModeForLoggerClassname(String classname) {
        debug("[turnOffDebugModeForLoggerClassname] {}", classname);
        return debugLogsDecorator.turnOffDebugModeForLoggerClassname(classname);
    }

    public boolean turnOffDebugModeForLoggerClass(Class c) {
        debug("[turnOffDebugModeForLoggerClass] {}", c);
        return debugLogsDecorator.turnOffDebugModeForLoggerClass(c);
    }
    public boolean clearDebugLoggers() {
        debug("[clearDebugLoggers]");
        debugLogsDecorator.clearDebugLoggers();
        return true;
    }

    public String[] collectLoggerNames() {
        debug("[collectLoggerNames] {}");
        return debugLogsDecorator.collectLoggerNames();
    }

    public boolean turnOnDebugForClasses(Collection<Class> classCollection) {
        debug("[turnOnDebugForClasses]");
        boolean r = true;
        for (Class c : classCollection) {
            r = turnOnDebugModeForLoggerClass(c) && r;
        }
        return r;
    }

    public boolean turnOnDebugForClassNames(Collection<String> classCollection) {
        debug("[turnOnDebugForClassNames]");
        boolean r = true;
        for (String name : classCollection) {
            r = turnOnDebugModeForLoggerClassname(name) && r;
        }
        return r;
    }

    public boolean turnOffDebugForClasses(Collection<Class> classCollection) {
        debug("[turnOffDebugForClasses]");
        boolean r = true;
        for (Class c : classCollection) {
            r = turnOffDebugModeForLoggerClass(c) && r;
        }
        return r;
    }

    public boolean turnOffDebugForClassNames(Collection<String> classCollection) {
        debug("[turnOffDebugForClassNames]");
        boolean r = true;
        for (String name : classCollection) {
            r = turnOffDebugModeForLoggerClassname(name) && r;
        }
        return r;
    }

    public boolean setDebugForClasses(Collection<Class> classes) {
        debug("[setDebugForClasses]");
        boolean result = true;
        Set<String> classSet = classes.stream().map(Class::getName).collect(Collectors.toSet());
        return setDebugForClassNames(classSet);
    }

    public boolean setDebugForClassNames(Collection<String> classes) {
        debug("[setDebugForClassNames]");
        boolean result = true;
        Set<String> classSet = new HashSet<>(classes);
        Set<String> workingClasses = this.debugLogsDecorator.getDebugLoggers().stream()
                .map(Class::getName)
                .collect(Collectors.toSet());
        for (String name : workingClasses) {
            if (!classSet.contains(name)) {
                result = turnOffDebugModeForLoggerClassname(name) && result;
            } else {
                classSet.remove(name);
            }
        }
        for (String name : classSet) {
            result = turnOnDebugModeForLoggerClassname(name) && result;
        }
        return result;
    }

    public boolean saveConfig(int buffer, Collection<String> classes) {
        debug("[saveConfig]");
        boolean result = true;
        result = setMaxEntries(buffer) && result;
        result = setDebugForClassNames(classes) && result;
        return result;
    }

    public List<Map.Entry<String, String>> getDebugEntriesAsList() {
        debug("[getDebugEntriesAsList]");
        ArrayList<Map.Entry<String, String>> a = new ArrayList<>(debugLogsDecorator.getDebugEntries().entrySet());
        Collections.reverse(a);
        return a;
    }

    public Set<Class> getDebugLoggers() {
        debug("[getDebugLoggers]");
        return debugLogsDecorator.getDebugLoggers();
    }

    public Set<String> getDebugLoggersNames() {
        debug("[getDebugLoggers]");
        return debugLogsDecorator.getDebugLoggers().stream().map(Class::getName).collect(Collectors.toSet());
    }

    public List<Class> getAvailableLoggers() {
        debug("[getAvailableLoggers]");
        return debugLogsDecorator.getAvailableLoggers();
    }
}
