/*
MIT License

Copyright (c) 2014-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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

package com.openkoda.core.flow.mbean;

import com.openkoda.core.flow.LoggingComponent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Map.Entry;

public class DebugLogsDecorator implements StatisticsMBean, LoggingComponent {

	@Override
	public boolean turnOnDebugModeForLoggerClassname(String classname) {
		debug("[turnOnDebugModeForLoggerClassname] {}", classname);
		if(StringUtils.isBlank(classname)) return false;
		for (Entry<Class, Logger> e : loggers.entrySet()) {
			if(classname.equals(e.getKey().getName())) {
				return turnOnDebugModeForLoggerClass(e.getKey());
			}
		}
		return false;
	}

	public boolean turnOnDebugModeForLoggerClass(Class c) {
		debug("[turnOnDebugModeForLoggerClass] {}", c);
		if(c == null) {
			return false;
		}
		return debugLoggers.add(c);
	}

	@Override
	public boolean turnOffDebugModeForLoggerClassname(String classname) {
		debug("[turnOffDebugModeForLoggerClassname] {}", classname);
		for (Entry<Class, Logger> e : loggers.entrySet()) {
			if(classname.equals(e.getKey().getName())) {
				return turnOffDebugModeForLoggerClass(e.getKey());
			}
		}
		return false;
	}

	public boolean turnOffDebugModeForLoggerClass(Class c) {
		if(c == null) {
			return false;
		}
		return debugLoggers.remove(c);
	}

	@Override
	public void clearDebugLoggers() {
		debugLoggers.clear();
	}
	
	@Override
	public String[] collectLoggerNames() {
		return availableLoggers.stream().map(a -> a.getName()).toArray(String[]::new);
	}

	public boolean setMaxEntries(int maxEntries) {
		debugStack.setMaxEntries(maxEntries);
		return true;
	}

}
