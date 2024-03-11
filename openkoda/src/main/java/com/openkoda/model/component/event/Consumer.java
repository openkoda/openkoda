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

package com.openkoda.model.component.event;

/**
 * Helper Consumer object to perform mapping between {@link com.openkoda.form.EventListenerForm} adn {@link EventListenerEntry}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-12
 */
public class Consumer {

    private String className;
    private String methodName;
    private String parameterType;
    private int numberOfStaticParams;
    private String category;

    public Consumer(String consumerString) {
        String[] consumerSplit = consumerString.split(",");
        if(consumerSplit.length == 4) {
            this.className = consumerSplit[0];
            this.methodName = consumerSplit[1];
            this.parameterType = consumerSplit[2];
            this.numberOfStaticParams= Integer.valueOf(consumerSplit[3]);
        }
    }

    public static String canonicalMethodName(String className, String methodName, String parameterType, int numberOfStaticParams) {
        return className + "," + methodName + "," + parameterType + "," + numberOfStaticParams;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public int getNumberOfStaticParams() {
        return numberOfStaticParams;
    }

    public void setNumberOfStaticParams(int numberOfStaticParams) {
        this.numberOfStaticParams = numberOfStaticParams;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Consumer[" + className + ',' + methodName + ',' + parameterType + ',' + numberOfStaticParams + ']';
    }
}
