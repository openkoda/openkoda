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

package com.openkoda.core.service.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The class responsible for helping to manage events
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
public class AbstractApplicationEvent<T> {

    private final Class<T> eventClass;
    private final String eventName;
    private final static Map<String, AbstractApplicationEvent> eventList = new HashMap<>();


    /**
     * Constructor of the AbstractApplicationEvent class/
     * @param eventClass
     * @param eventName
     */
    protected AbstractApplicationEvent(Class<T> eventClass, String eventName) {
        this.eventClass = eventClass;
        this.eventName = eventName;
        eventList.put(eventName, this);
    }

    /**
     * The equals() method is used to compare two objects for equality.
     *
     * Two objects of this class are considered equal if they have the same eventClass and eventName fields
     * @param o object to compare
     * @return the result of a boolean comparison of whether objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractApplicationEvent<?> that = (AbstractApplicationEvent<?>) o;
        return Objects.equals(eventClass, that.eventClass) &&
                Objects.equals(eventName, that.eventName);
    }

    /**
     * Method is used to generate the hash code based on the eventClass and eventName fields.
     *
     * By implementing the hashCode() method in this way, the hash code for an object of this class
     * will be based on the values of its eventClass and eventName fields.
     * This ensures that two objects that are equal according to their equals() method will also have the same hash code.
     * This is important for correctness when using hash-based data structures such as HashMap and HashSet.
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventClass, eventName);
    }

    /**
     *
     * @return the AbstractApplicationEvent object that corresponds to the eventName parameter.
     * If there is no such event in the eventList collection, the method will return null.
     */
    public static AbstractApplicationEvent getEvent(String eventName){
        return eventList.get(eventName);
    }
}
