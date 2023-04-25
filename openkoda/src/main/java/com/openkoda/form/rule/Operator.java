/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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

package com.openkoda.form.rule;

import com.openkoda.model.OptionWithLabel;

public enum Operator implements OptionWithLabel {

    equals("==", " == '%s'", true, true),
    notEquals("!=", " != '%s'", true, true),
    contains("Contains", ".contains('%s')", true, false),
    in("In", ".contains(%s)", true, true),
    greaterThan(">", " > '%f'", false, true),
    lessThan("<", " < '%f'", false, true),
    ;

    private String label;
    private String spel;
    private Boolean stringsOperator;
    private Boolean numbersOperator;

    Operator(String label, String spel, Boolean stringsOperator, Boolean numbersOperator) {
        this.label = label;
        this.spel = spel;
        this.stringsOperator = stringsOperator;
        this.numbersOperator = numbersOperator;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSpel() {
        return spel;
    }

    public void setSpel(String spel) {
        this.spel = spel;
    }

    public Boolean getStringsOperator() {
        return stringsOperator;
    }

    public void setStringsOperator(Boolean stringsOperator) {
        this.stringsOperator = stringsOperator;
    }

    public Boolean getNumbersOperator() {
        return numbersOperator;
    }

    public void setNumbersOperator(Boolean numbersOperator) {
        this.numbersOperator = numbersOperator;
    }

    public static Operator fromString(String text) {
        for(Operator operator : Operator.values()) {
            if(operator.label.equalsIgnoreCase(text)) {
                return operator;
            }
        }
        return null;
    }
}
