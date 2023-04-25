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

package com.openkoda.service;

import com.openkoda.AbstractTest;
import com.openkoda.core.flow.LoggingComponent;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class RulesEngineTest extends AbstractTest {

    public static class SomeEvent{
        public String name;

        public SomeEvent(String name) {
            this.name = name;
        }
    }


    public static void main(String[] args) {


        SpelExpressionParser parser = new SpelExpressionParser();

        String ifClause = "name == 'pucio'";
        String ifClause2 = "name =='puciopucio'";
        String thenClause = "category.contains('futerały')";
        String elseClause = "name.contains('xiaomi')";

        String wyrazenieDoZapisaniaWBazie = ifClause + " ? " + thenClause + " : " + elseClause;

        SpelExpression exp = parser.parseRaw(wyrazenieDoZapisaniaWBazie);

        SpelNode ast = exp.getAST();


        SpelNode ifChild = ast.getChild(0);
        LoggingComponent.debugLogger.debug(ifChild.toStringAST());

        SpelExpression ifClauseExp = parser.parseRaw(ifChild.toStringAST());

        //pucio

        StandardEvaluationContext ctx = new StandardEvaluationContext(new SomeEvent("pucio"));
        Boolean  result = (Boolean) ifClauseExp.getValue(ctx);
        LoggingComponent.debugLogger.debug(result.toString());

        //nie pucio
        ctx = new StandardEvaluationContext(new SomeEvent("iphone"));
        result = (Boolean) ifClauseExp.getValue(ctx);
        LoggingComponent.debugLogger.debug(result.toString());






    }
}
