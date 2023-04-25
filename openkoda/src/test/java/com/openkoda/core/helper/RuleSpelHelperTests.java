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

package com.openkoda.core.helper;

import com.openkoda.AbstractTest;
import com.openkoda.dto.RuleDto;
import com.openkoda.form.rule.LogicalOperator;
import com.openkoda.form.rule.Operator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RuleSpelHelperTests extends AbstractTest {

    @Test
    public void shouldParseSimpleRuleToString() {
//        given
        RuleDto ruleDto = initSimpleDto();

//        when
        String rule = RuleSpelHelper.parseToString(ruleDto);

//        then
        assertEquals("name == 'test' ? category == 'test2' : category.contains('test3')", rule);
    }

    @Test
    public void shouldParseRuleToString() {
//        given
        RuleDto ruleDto = initSimpleDto();

        ruleDto.getIfStatements().put(1L, new TreeMap<>());
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.LogicalOperator, "or");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Field, "name");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Operator, "contains");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Value, "test1");

//        when
        String rule = RuleSpelHelper.parseToString(ruleDto);

//        then
        assertEquals("name == 'test' or name.contains('test1') ? category == 'test2' : category.contains('test3')", rule);
    }

    @Test
    public void shouldParseEmptyRuleDto() {
//        given
        RuleDto emptyRuleDto = new RuleDto();
//        when
        String rule = RuleSpelHelper.parseToString(emptyRuleDto);
//        then
        assertEquals("", rule);
    }

    @Test
    public void shouldParseMultipleConditionsRuleDto() {
//        given
        RuleDto ruleDto = initSimpleDto();

        ruleDto.getIfStatements().put(1L, new TreeMap<>());
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.LogicalOperator, "or");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Field, "name");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Operator, "contains");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Value, "test1");
        ruleDto.getThenStatements().put(1L, new TreeMap<>());
        ruleDto.getThenStatements().get(1L).put(RuleDto.StatementKey.LogicalOperator, "or");
        ruleDto.getThenStatements().get(1L).put(RuleDto.StatementKey.Field, "name");
        ruleDto.getThenStatements().get(1L).put(RuleDto.StatementKey.Operator, "contains");
        ruleDto.getThenStatements().get(1L).put(RuleDto.StatementKey.Value, "test8");
        ruleDto.getThenStatements().put(2L, new TreeMap<>());
        ruleDto.getThenStatements().get(2L).put(RuleDto.StatementKey.LogicalOperator, "or");
        ruleDto.getThenStatements().get(2L).put(RuleDto.StatementKey.Field, "category");
        ruleDto.getThenStatements().get(2L).put(RuleDto.StatementKey.Operator, "in");
        ruleDto.getThenStatements().get(2L).put(RuleDto.StatementKey.Value, "test5,test6");
        ruleDto.getElseStatements().put(1L, new TreeMap<>());
        ruleDto.getElseStatements().get(1L).put(RuleDto.StatementKey.LogicalOperator, "and");
        ruleDto.getElseStatements().get(1L).put(RuleDto.StatementKey.Field, "name");
        ruleDto.getElseStatements().get(1L).put(RuleDto.StatementKey.Operator, "notEquals");
        ruleDto.getElseStatements().get(1L).put(RuleDto.StatementKey.Value, "test7");

//        when
        String rule = RuleSpelHelper.parseToString(ruleDto);

//        then
        assertEquals("name == 'test' or name.contains('test1') ? category == 'test2' or name.contains('test8') or {'test5','test6'}.contains(category) : category.contains('test3') and name != 'test7'", rule);
    }

    @Test
    public void shouldParseRuleDtoWithoutElse() {
//        given
        RuleDto ruleDto = initSimpleDto();

        ruleDto.getIfStatements().put(1L, new TreeMap<>());
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.LogicalOperator, "or");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Field, "name");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Operator, "contains");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Value, "test1");
        ruleDto.getThenStatements().put(1L, new TreeMap<>());
        ruleDto.getThenStatements().get(1L).put(RuleDto.StatementKey.LogicalOperator, "or");
        ruleDto.getThenStatements().get(1L).put(RuleDto.StatementKey.Field, "name");
        ruleDto.getThenStatements().get(1L).put(RuleDto.StatementKey.Operator, "contains");
        ruleDto.getThenStatements().get(1L).put(RuleDto.StatementKey.Value, "test8");
        ruleDto.getElseStatements().get(0L).put(RuleDto.StatementKey.Operator, "");
//        when
        String rule = RuleSpelHelper.parseToString(ruleDto);

//        then
        assertEquals("name == 'test' or name.contains('test1') ? category == 'test2' or name.contains('test8') : null", rule);

    }

    @Test
    public void shouldNotParseRuleDtoWithValueNotMatchingRegex() {
        // given
        RuleDto ruleDto = initSimpleDto();
        ruleDto.getIfStatements().put(1L, new TreeMap<>());
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.LogicalOperator, "or");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Field, "name");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Operator, "contains");
        ruleDto.getIfStatements().get(1L).put(RuleDto.StatementKey.Value, "test1 or 1=1");

        // when
        Executable ruleSpelHelperExecutable = () -> RuleSpelHelper.parseToString(ruleDto);

        // then
        assertThrows(RuntimeException.class, ruleSpelHelperExecutable);
    }

    @Test
    public void shouldParseEmptyRuleString() {
//        given
        String rule = "";
//        when
        RuleDto ruleDto = RuleSpelHelper.parseToRuleDto(rule);
//        then
        assertEquals(1, ruleDto.getIfStatements().size());
        Assertions.assertNull(ruleDto.getIfStatements().get(0L).get(RuleDto.StatementKey.Operator));
    }

    @Test
    public void shouldParseSimpleRuleString() {
//        given
        String rule = "name == 'test' ? category == 'test2' : category.contains('test3')";
//        when
        RuleDto ruleDto = RuleSpelHelper.parseToRuleDto(rule);
//        then
        assertEquals(1, ruleDto.getIfStatements().size());
        assertEquals(Operator.equals, ruleDto.getIfStatements().get(0L).get(RuleDto.StatementKey.Operator));
        assertEquals(Operator.contains, ruleDto.getElseStatements().get(0L).get(RuleDto.StatementKey.Operator));
    }

    @Test
    public void shouldParseNullElseRuleString() {
//        given
        String rule = "name == 'test' ? category == 'test2' : null";
//        when
        RuleDto ruleDto = RuleSpelHelper.parseToRuleDto(rule);
//        then
        assertEquals(1, ruleDto.getIfStatements().size());
        assertEquals(Operator.equals, ruleDto.getIfStatements().get(0L).get(RuleDto.StatementKey.Operator));
        Assertions.assertNull(ruleDto.getElseStatements().get(0L).get(RuleDto.StatementKey.Operator));
    }

    @Test
    public void shouldParseMultipleConditionsRuleString() {
//        given
        String rule = "name == 'test' or name.contains('test1') ? category == 'test2' or name.contains('test8') and {'test5','test6'}.contains(category) : category.contains('test3') and name != 'test7'";
//        when
        RuleDto ruleDto = RuleSpelHelper.parseToRuleDto(rule);
//        then
        assertEquals(2, ruleDto.getIfStatements().size());
        assertEquals(3, ruleDto.getThenStatements().size());
        assertEquals(2, ruleDto.getElseStatements().size());
        assertEquals(Operator.contains, ruleDto.getIfStatements().get(1L).get(RuleDto.StatementKey.Operator));
        assertEquals(Operator.in, ruleDto.getThenStatements().get(2L).get(RuleDto.StatementKey.Operator));
        assertEquals("test5,test6", ruleDto.getThenStatements().get(2L).get(RuleDto.StatementKey.Value));
        assertEquals(LogicalOperator.and, ruleDto.getThenStatements().get(2L).get(RuleDto.StatementKey.LogicalOperator));
        assertEquals(Operator.notEquals, ruleDto.getElseStatements().get(1L).get(RuleDto.StatementKey.Operator));
    }

    @Test
    public void shouldValidateCorrectSimpleRule() {
//        given
        String rule = "name == 'test' ? category == 'test2' : category.contains('test3')";
//        when
        boolean isValid = RuleSpelHelper.isRuleValid(rule);
//        then
        Assertions.assertTrue(isValid);
    }

    @Test
    public void shouldValidateCorrectMultipleConditionsRule() {
//        given
        String rule = "name == 'test' or name.contains('test1') ? category == 'test2' or name.contains('test8') or {'test5','test6'}.contains(category) : category.contains('test3') and name != 'test7'";
//        when
        boolean isValid = RuleSpelHelper.isRuleValid(rule);
//        then
        Assertions.assertTrue(isValid);
    }

    @Test
    public void shouldValidateCorrectNoElseRule() {
//        given
        String rule = "name == 'test' or name.contains('test1') ? category == 'test2' or name.contains('test8') : null";
//        when
        boolean isValid = RuleSpelHelper.isRuleValid(rule);
//        then
        Assertions.assertTrue(isValid);
    }

    @Test
    public void shouldNotValidateIncorrectRule() {
//        given
        String rule = "name.contains('test1') ? category == 'test2' or name.contains('test8') : category in {'test'}";
//        when
        boolean isValid = RuleSpelHelper.isRuleValid(rule);
//        then
        Assertions.assertFalse(isValid);
    }

    @Test
    public void shouldGetDbSelectForRule() {
//        given
        String rule = "name == 'test' ? category == 'test2' : category.contains('test3')";
        String tableName = "items";
//        when
        String select = RuleSpelHelper.getSelect(rule, tableName);
//        then
        assertEquals("SELECT CASE WHEN (name == 'test') THEN (category == 'test2') ELSE category.contains('test3') FROM items", select);
    }

    @Test
    public void shouldGetDbSelectForMultipleConditionsRule() {
//        given
        String rule = "name == 'test' or name.contains('test1') ? category == 'test2' or name.contains('test8') or {'test5','test6'}.contains(category) : category.contains('test3') and name != 'test7'";
        String tableName = "items";
//        when
        String select = RuleSpelHelper.getSelect(rule, tableName);
//        then
        assertEquals("SELECT CASE WHEN ((name == 'test') or name.contains('test1')) THEN (((category == 'test2') or name.contains('test8')) or {'test5','test6'}.contains(category)) ELSE (category.contains('test3') and (name != 'test7')) FROM items", select);
    }

    @Test
    public void shouldNotGetDbSelectForIncorrectRule() {
//        given
        String rule = "name == 'test'";
        String tableName = "items";
//        when
        String select = RuleSpelHelper.getSelect(rule, tableName);
//        then
        assertEquals("", select);
    }

    private RuleDto initSimpleDto() {
        RuleDto ruleDto = new RuleDto();
//        IF
        ruleDto.getIfStatements().get(0L).put(RuleDto.StatementKey.Field, "name");
        ruleDto.getIfStatements().get(0L).put(RuleDto.StatementKey.Operator, "equals");
        ruleDto.getIfStatements().get(0L).put(RuleDto.StatementKey.Value, "test");
//        THEN
        ruleDto.getThenStatements().get(0L).put(RuleDto.StatementKey.Field, "category");
        ruleDto.getThenStatements().get(0L).put(RuleDto.StatementKey.Operator, "equals");
        ruleDto.getThenStatements().get(0L).put(RuleDto.StatementKey.Value, "test2");
//        ELSE
        ruleDto.getElseStatements().get(0L).put(RuleDto.StatementKey.Field, "category");
        ruleDto.getElseStatements().get(0L).put(RuleDto.StatementKey.Operator, "contains");
        ruleDto.getElseStatements().get(0L).put(RuleDto.StatementKey.Value, "test3");

        return ruleDto;
    }

}
