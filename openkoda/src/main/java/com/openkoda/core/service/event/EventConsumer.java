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

import com.openkoda.core.helper.ApplicationContextProvider;
import com.openkoda.core.helper.NameHelper;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import org.apache.commons.lang3.StringUtils;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventConsumer<T> implements LoggingComponentWithRequestId {

    private final Consumer<T> consumer;
    private final BiConsumer<T, String []> consumerWithStaticData;
    private final Method consumerMethod;
    private final String description;
    private final String numberOfConsumerMethodParameters;
    private final String methodDescription;
    private final Class<T> eventClass;


    /**
     * EventConsumer class constructor
     */
    public EventConsumer(Consumer<T> consumer) {
        this.consumer = consumer;
        this.consumerWithStaticData = null;
        this.consumerMethod = null;
        this.description = "";
        this.eventClass = null;
        String dryMethodDescription = NameHelper.createMethodDescription(consumerMethod);
        this.methodDescription = dryMethodDescription.replaceAll(",String", "").replaceAll(", String", "");
        this.numberOfConsumerMethodParameters = String.valueOf(StringUtils.countMatches(dryMethodDescription, ", String"));
    }
    /**
     * EventConsumer class constructor
     */
    public EventConsumer(Method method,
                         Class<T> eventClass,
                         int numberOfConsumerMethodParameters) {
        this.consumer = null;
        this.consumerWithStaticData = null;
        this.consumerMethod = method;
        this.eventClass = eventClass;
        this.description = "";
        String dryMethodDescription = NameHelper.createMethodDescription(consumerMethod);
        this.methodDescription = dryMethodDescription.replaceAll(",String", "").replaceAll(", String", "");
        this.numberOfConsumerMethodParameters = String.valueOf(numberOfConsumerMethodParameters);
    }

    /**
     * EventConsumer class constructor
     */
    public EventConsumer(Method method,
                         Class<T> eventClass,
                         int numberOfConsumerMethodParameters, String description) {
        this.consumer = null;
        this.consumerWithStaticData = null;
        this.consumerMethod = method;
        this.eventClass = eventClass;
        this.description = description;
        String dryMethodDescription = NameHelper.createMethodDescription(consumerMethod);
        this.methodDescription = dryMethodDescription.replaceAll(",String", "").replaceAll(", String", "");
        this.numberOfConsumerMethodParameters = String.valueOf(numberOfConsumerMethodParameters);
    }

    /**
     * EventConsumer class constructor
     */
    public EventConsumer(BiConsumer<T, String[]> consumerWithStaticData) {
        this.consumerWithStaticData = consumerWithStaticData;
        this.consumer = null;
        this.consumerMethod = null;
        this.description = "";
        this.eventClass = null;
        String dryMethodDescription = NameHelper.createMethodDescription(consumerMethod);
        this.methodDescription = dryMethodDescription.replaceAll(", String", "");
        this.numberOfConsumerMethodParameters = String.valueOf(StringUtils.countMatches(dryMethodDescription, ", String"));
    }

    /**
     * @param eventObject
     * @param staticParameter
     * @return returns true if it was able to successfully consume the event
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public boolean accept(T eventObject, String ... staticParameter) {
        debug("[accept] eventObject: {} parameters: {}", eventObject, staticParameter);
        if (consumer != null) {
            consumer.accept(eventObject);
            return true;
        }
        if (consumerWithStaticData != null) {
            consumerWithStaticData.accept(eventObject, staticParameter);
            return true;
        }
        if (consumerMethod != null) {
            try {
                Object consumerObj = ApplicationContextProvider.getContext().getBean(consumerMethod.getDeclaringClass());
                if (staticParameter != null) {
                    invokeConsumerMethod(consumerMethod, consumerObj, eventObject, staticParameter);
                } else {
                    consumerMethod.invoke(consumerObj, eventObject);
                }
            } catch (IllegalAccessException e) {
                error(e, "Could not invoke consumer method {}:{} due to {}",
                        consumerMethod.getDeclaringClass().getName(), consumerMethod.getName(), e.getMessage());
            } catch (InvocationTargetException e) {
                error(e, "Could not invoke consumer method {}:{} due to {}",
                        consumerMethod.getDeclaringClass().getName(), consumerMethod.getName(), e.getTargetException().getStackTrace()[0].toString());
            }
        }
        return true;
    }

    /**
     * This method is used to invoke the consumer method.
     * @param consumerMethod
     * @param consumerObj
     * @param eventObject
     * @param staticParameter
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void invokeConsumerMethod(Method consumerMethod, Object consumerObj, T eventObject, String[] staticParameter) throws InvocationTargetException, IllegalAccessException {
        debug("[invokeConsumerMethod] method: {} consumer: {} event: {} parameters: {}", consumerMethod, consumerObj, eventObject, staticParameter);
        if(staticParameter.length == 1){
            this.consumerMethod.invoke(consumerObj, eventObject, staticParameter[0]);
        }
        if(staticParameter.length == 2){
            this.consumerMethod.invoke(consumerObj, eventObject, staticParameter[0], staticParameter[1]);
        }
        if(staticParameter.length == 3){
            this.consumerMethod.invoke(consumerObj, eventObject, staticParameter[0], staticParameter[1], staticParameter[2]);
        }
        if(staticParameter.length == 4){
            this.consumerMethod.invoke(consumerObj, eventObject, staticParameter[0], staticParameter[1], staticParameter[2], staticParameter[3]);
        }
    }

    /**
     * @return EventConsumer consumerMethod
     */
    public Method getConsumerMethod() {
        return consumerMethod;
    }
    /**
     * @return EventConsumer description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param className
     * @param methodName
     * @param eventObjectClass
     * @param numberOfStaticMethodParameters
     * @return true indicating that the method matches the expected criteria.
     */
    public boolean verifyMethod(String className, String methodName, Class eventObjectClass, int numberOfStaticMethodParameters) {
        debug("[verifyMethod] class: {} method: {} event: {} numOfParams: {}", className, methodName, eventObjectClass, numberOfStaticMethodParameters);
        return this.consumerMethod != null
                && this.consumerMethod.getDeclaringClass().getName().equals(className)
                && this.consumerMethod.getName().equals(methodName)
                && this.eventClass.isAssignableFrom(eventObjectClass)
                && this.numberOfConsumerMethodParameters.equals(String.valueOf(numberOfStaticMethodParameters));
    }

    /**
     * @return a Tuple3 object with three values: methodDescription, numberOfConsumerMethodParameters, and description.
     */
    public Tuple3<String, String, String> propertiesToTuple() {
        return Tuples.of(this.methodDescription, this.numberOfConsumerMethodParameters, this.description != null ? this.description : "");
    }
}
