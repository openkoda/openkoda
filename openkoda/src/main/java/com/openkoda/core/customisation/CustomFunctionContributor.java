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

package com.openkoda.core.customisation;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.sql.ast.SqlAstNodeRenderingMode;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.sql.ast.tree.expression.QueryLiteral;
import org.hibernate.type.StandardBasicTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Function Contributor, that registers custom PostreSQL functions
 */
public class CustomFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry().register("string_agg", new StandardSQLFunction("string_agg", StandardBasicTypes.STRING));
        functionContributions.getFunctionRegistry().register("arrays_suffix", new ArraysSuffixFunction());
        functionContributions.getFunctionRegistry().register("arrays_overlap", new StringArraysOverlapFunction());
    }

    /**
     * Function that checks if two postgres arrays overlap.
     * It can be used to check if organizations with privilege are in entity related organizations.
     * This allows to make security check SQL server-side
     */

    public class StringArraysOverlapFunction extends AbstractSqmSelfRenderingFunctionDescriptor{

        public StringArraysOverlapFunction() {
            super("arrays_overlap", StandardArgumentsValidators.exactly(2), null, null);
        }

        @Override
        public void render(SqlAppender sqlAppender, List<? extends SqlAstNode> sqlAstArguments, SqlAstTranslator<?> walker) {
            final Expression arg1 = (Expression) sqlAstArguments.get(0);
            final QueryLiteral arg2 = (QueryLiteral) sqlAstArguments.get(1);

            sqlAppender.appendSql("(ARRAY[");
            if(arg2.getLiteralValue() instanceof ArrayList) {
                appendStringArrayList(sqlAppender, arg2);
            }
            else if(arg2.getLiteralValue() instanceof HashSet){
                appendLongHashSet(sqlAppender, arg2);
            } else {
                throw new IllegalArgumentException("The function cannot handle arg2 with type " + arg2.getLiteralValue().getClass());
            }
            walker.render(arg1, SqlAstNodeRenderingMode.DEFAULT);
            sqlAppender.appendSql( ")");

        }
        private void appendStringArrayList(SqlAppender sqlAppender, QueryLiteral arg2){
            sqlAppender.appendSql(getAsCommaSeparatedAndSingleQuotedString((ArrayList<String>) arg2.getLiteralValue()));
            sqlAppender.appendSql("]");
            sqlAppender.appendSql("::varchar[] && ");
        }

        private void appendLongHashSet(SqlAppender sqlAppender, QueryLiteral arg2){
            sqlAppender.appendSql(getAsCommaSeparatedString((HashSet<Long>) arg2.getLiteralValue()));
            sqlAppender.appendSql("]");
            sqlAppender.appendSql("::bigint[] && ");
        }

        private String getAsCommaSeparatedString(HashSet<Long> values){
            return String.join(",", values.stream().map(value -> String.valueOf(value)).collect(Collectors.toList()));
        }

        private String getAsCommaSeparatedAndSingleQuotedString(ArrayList<String> values){
            return String.join(",", values.stream()
                    .map(value -> ("'" + value + "'"))
                    .collect(Collectors.toList()));
        }
    }

    /**
     * Function that adds a suffix to each value of a postgres array.
     */
    public class ArraysSuffixFunction extends AbstractSqmSelfRenderingFunctionDescriptor{

        public ArraysSuffixFunction(){
            super("arrays_suffix", StandardArgumentsValidators.exactly(2), null, null);
        }

        @Override
        public void render(SqlAppender sqlAppender, List<? extends SqlAstNode> sqlAstArguments, SqlAstTranslator<?> walker) {
            final Expression arg1 = (Expression) sqlAstArguments.get(0);
            final Expression arg2 = (Expression) sqlAstArguments.get(1);

            sqlAppender.appendSql("(array(select (unnest(");
            walker.render(arg1, SqlAstNodeRenderingMode.DEFAULT);
            sqlAppender.appendSql(") || ");
            walker.render(arg2, SqlAstNodeRenderingMode.DEFAULT);
            sqlAppender.appendSql(")::varchar))");

        }
    }
}
