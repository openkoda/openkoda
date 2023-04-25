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

package com.openkoda.core.flow;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PageAttr<T> {

	private final static Map<String, PageAttr> allByName = new HashMap<>();

	public final String name;
	public final Supplier<T> constructor;

	public PageAttr(String name) {
		this(name, () -> null);
	}

	public PageAttr(String name, Supplier<T> constructor) {
		Assert.isTrue(!allByName.containsKey(name), "Page Attribute Collision: " + name);
		this.constructor = constructor;
        this.name = name;
		allByName.put(name, this);
	}

	public static PageAttr getByName(String name) {
	    return allByName.get(name);
    }
}
