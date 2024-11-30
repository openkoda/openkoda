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

package com.openkoda.core.helper;

import com.openkoda.dto.RuleDto;
import com.openkoda.form.rule.LogicalOperator;
import com.openkoda.form.rule.Operator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.InlineList;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RuleSpelHelper {

    public static final String QUESTION_MARK = " ? ";
    public static final String COLON = " : ";
    public static final String VALUE_APOSTROPHE = "'";
    public static final String VALUE_REGEX = "^([a-zA-Z0-9,. _-]+)$";
    public static final String EMPTY_ELSE = "null";

    public static String parseToString(RuleDto ruleDto) {
        if(ruleDto.getIfStatements().get(0L).get(RuleDto.StatementKey.Operator) != null
                && ruleDto.getThenStatements().get(0L).get(RuleDto.StatementKey.Operator) != null) {
            String ruleIf = concatRulePart(ruleDto.getIfStatements());
            String ruleThen = concatRulePart(ruleDto.getThenStatements());
            String ruleElse = EMPTY_ELSE;
            if(ruleDto.getElseStatements().get(0L).get(RuleDto.StatementKey.Operator) != null
                && StringUtils.isNotEmpty(ruleDto.getElseStatements().get(0L).get(RuleDto.StatementKey.Operator).toString())) {
                ruleElse = concatRulePart(ruleDto.getElseStatements());
            }
            return ruleIf + QUESTION_MARK + ruleThen + COLON + ruleElse;
        }
        return "";
    }

    public static RuleDto parseToRuleDto(String rule) {
        RuleDto ruleDto = new RuleDto();
        if(StringUtils.isEmpty(rule)) {
            return ruleDto;
        }

        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression exp = parser.parseRaw(rule);

        ruleDto.setIfStatements(parse(0, new TreeMap<>(), null, "", Collections.emptyList(), null, exp.getAST().getChild(0)).getT1());
        ruleDto.setThenStatements(parse(0, new TreeMap<>(), null, "", Collections.emptyList(), null, exp.getAST().getChild(1)).getT1());
        if(exp.getAST().getChild(2) != null) {
            ruleDto.setElseStatements(parse(0, new TreeMap<>(), null, "", Collections.emptyList(), null, exp.getAST().getChild(2)).getT1());
        }

        return ruleDto;
    }

    public static boolean isRuleValid(String rule) {
        SpelExpressionParser parser = new SpelExpressionParser();
        try {
            parser.parseExpression(rule);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String getSelect(String rule, String tableName) {
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression exp = parser.parseRaw(rule);
        SpelNode ast = exp.getAST();
        return ast.getChildCount() == 3 ? "SELECT CASE WHEN " + ast.getChild(0).toStringAST()
                + " THEN " + ast.getChild(1).toStringAST()
                + " ELSE " + ast.getChild(2).toStringAST() +
                " FROM " + tableName : "";
    }

    public static<R> Tuple2<Map<Long, Map<RuleDto.StatementKey, Object>>, Predicate> parse(long index, Map<Long, Map<RuleDto.StatementKey, Object>> ruleParts, Root<R> root, String fieldName, List<String> values, EntityManager entityManager, SpelNode ast) {
        ruleParts.computeIfAbsent(index, k -> new TreeMap<>());

        if(ast instanceof MethodReference) {
            return handleMethodReference(index, ruleParts, root, fieldName, values, entityManager, ast);
        }

        if(ast instanceof org.springframework.expression.spel.ast.Operator) {
            org.springframework.expression.spel.ast.Operator operator = (org.springframework.expression.spel.ast.Operator) ast;
            if(EnumUtils.isValidEnum(LogicalOperator.class, operator.getOperatorName())) {
                return handleLogicalOperator(index, ruleParts, root, fieldName, values, entityManager, operator);
            } else {
                ruleParts.get(index).put(RuleDto.StatementKey.Operator, Operator.fromString(operator.getOperatorName()));
            }
            if (operator.getLeftOperand().getChildCount() == 0 && operator.getRightOperand().getChildCount() == 0) {
                return handleOperands(index, ruleParts, root, entityManager, operator);
            }
        }

        for (int i = 0; i < ast.getChildCount(); i++) {
            if(ast.getChild(i) instanceof PropertyOrFieldReference) {
//                handle property field reference
                PropertyOrFieldReference propertyOrFieldReference = (PropertyOrFieldReference)ast.getChild(i);
                fieldName = propertyOrFieldReference.getName();
                if(ast.getChild(i).getChildCount() == 0) {
                    return parse(index , ruleParts, root, fieldName, values, entityManager, ast.getChild(i + 1));
                }
            }
            if(ast.getChild(i) instanceof InlineList) {
//                handle inline list
                InlineList inlineList = (InlineList)ast.getChild(i);
                values = inlineList.getConstantValue()
                        .stream().map(o -> o.toString().replaceAll(VALUE_APOSTROPHE, ""))
                        .collect(Collectors.toList());
                return parse(index , ruleParts, root, fieldName, values, entityManager, ast.getChild(i + 1));
            }
            return parse(index, ruleParts, root, fieldName, values, entityManager, ast.getChild(i));
        }

        return Tuples.of(ruleParts, entityManager != null ? entityManager.getCriteriaBuilder().disjunction() : null);
    }

    private static <R> Tuple2<Map<Long, Map<RuleDto.StatementKey, Object>>, Predicate> handleOperands(long index, Map<Long, Map<RuleDto.StatementKey, Object>> ruleParts, Root<R> root, EntityManager entityManager, org.springframework.expression.spel.ast.Operator operator) {
        ruleParts.get(index).put(RuleDto.StatementKey.Field, operator.getLeftOperand().toStringAST());
        ruleParts.get(index).put(RuleDto.StatementKey.Value, operator.getRightOperand().toStringAST()
                .replaceAll(VALUE_APOSTROPHE, ""));
        if (entityManager == null) {
            return Tuples.of(ruleParts, null);
        }
        if (Operator.fromString(operator.getOperatorName()).equals(Operator.equals)) {
            return Tuples.of(
                    ruleParts,
                    entityManager.getCriteriaBuilder()
                            .equal(
                                    root.get(operator.getLeftOperand().toStringAST()),
                                    operator.getRightOperand().toStringAST().replaceAll(VALUE_APOSTROPHE, ""))
            );
        } else if (Operator.fromString(operator.getOperatorName()).equals(Operator.notEquals)) {
            return Tuples.of(
                    ruleParts,
                    entityManager.getCriteriaBuilder()
                            .notEqual(
                                    root.get(operator.getLeftOperand().toStringAST()),
                                    operator.getRightOperand().toStringAST().replaceAll(VALUE_APOSTROPHE, ""))
            );
        } else if (Operator.fromString(operator.getOperatorName()).equals(Operator.greaterThan)) {
            return Tuples.of(
                    ruleParts,
                    entityManager.getCriteriaBuilder()
                            .greaterThan(
                                    root.get(operator.getLeftOperand().toStringAST()),
                                    operator.getRightOperand().toStringAST().replaceAll(VALUE_APOSTROPHE, ""))
            );
        } else if (Operator.fromString(operator.getOperatorName()).equals(Operator.lessThan)) {
            return Tuples.of(
                    ruleParts,
                    entityManager.getCriteriaBuilder()
                            .lessThan(
                                    root.get(operator.getLeftOperand().toStringAST()),
                                    operator.getRightOperand().toStringAST().replaceAll(VALUE_APOSTROPHE, ""))
            );
        }
        return null;
    }

    private static <R> Tuple2<Map<Long, Map<RuleDto.StatementKey, Object>>, Predicate> handleLogicalOperator(long index, Map<Long, Map<RuleDto.StatementKey, Object>> ruleParts, Root<R> root, String fieldName, List<String> values, EntityManager entityManager, org.springframework.expression.spel.ast.Operator operator) {
        long nextIndex = index + 1;
        ruleParts.computeIfAbsent(nextIndex, k -> new TreeMap<>());
        ruleParts.get(nextIndex).put(RuleDto.StatementKey.LogicalOperator, LogicalOperator.valueOf(operator.getOperatorName()));
        if(entityManager == null) {
            parse(index, ruleParts, root, fieldName, values, null, operator.getLeftOperand());
            parse(nextIndex, ruleParts, root, fieldName, values, null, operator.getRightOperand());
            return Tuples.of(ruleParts, null);
        }
        if (LogicalOperator.valueOf(operator.getOperatorName()).equals(LogicalOperator.and)) {
            return Tuples.of(
                    ruleParts,
                    entityManager.getCriteriaBuilder()
                            .and(
                                    parse(index, ruleParts, root, fieldName, values, entityManager, operator.getLeftOperand()).getT2(),
                                    parse(nextIndex, ruleParts, root, fieldName, values, entityManager, operator.getRightOperand()).getT2())
            );
        } else if (LogicalOperator.valueOf(operator.getOperatorName()).equals(LogicalOperator.or)) {
            return Tuples.of(
                    ruleParts,
                            entityManager.getCriteriaBuilder()
                                    .or(
                                            parse(index, ruleParts, root, fieldName, values, entityManager, operator.getLeftOperand()).getT2(),
                                            parse(nextIndex, ruleParts, root, fieldName, values, entityManager, operator.getRightOperand()).getT2())
            );
        }
        return null;
    }

    private static <R> Tuple2<Map<Long, Map<RuleDto.StatementKey, Object>>, Predicate> handleMethodReference(long index, Map<Long, Map<RuleDto.StatementKey, Object>> ruleParts, Root<R> root, String fieldName, List<String> values, EntityManager entityManager, SpelNode ast) {
        MethodReference method = (MethodReference) ast;
        if(EnumUtils.isValidEnum(Operator.class, method.getName())) {
            if(method.getName().equals(Operator.contains.name()) && ast.getChild(0) instanceof PropertyOrFieldReference) {
//                    custom IN operator here
                ruleParts.get(index).put(RuleDto.StatementKey.Operator, Operator.in);
                ruleParts.get(index).put(RuleDto.StatementKey.Field, ast.getChild(0).toStringAST());
                ruleParts.get(index).put(RuleDto.StatementKey.Value, String.join(",", values));
                return Tuples.of(
                        ruleParts,
                        root != null ?
                                root.get(ast.getChild(0).toStringAST()).in(values)
                                : null
                );
            } else {
//                    contains operator => like
                String cleanedAstValue =  ast.getChild(0).toStringAST().replaceAll(VALUE_APOSTROPHE, "");
                ruleParts.get(index).put(RuleDto.StatementKey.Field, fieldName);
                ruleParts.get(index).put(RuleDto.StatementKey.Operator, Operator.valueOf(method.getName()));
                ruleParts.get(index).put(RuleDto.StatementKey.Value, cleanedAstValue);
                return Tuples.of(
                        ruleParts,
                        entityManager != null ?
                                entityManager.getCriteriaBuilder()
                                        .like(
                                                root.get(fieldName),
                                                "%" + cleanedAstValue + "%")
                                : null);
            }
        }
        return null;
    }

    private static String concatRulePart(Map<Long, Map<RuleDto.StatementKey, Object>> statements) {
        StringBuilder ruleSB = new StringBuilder();
        for (Map.Entry<Long, Map<RuleDto.StatementKey, Object>> statementIndex : statements.entrySet()) {
            ruleSB.append(statementIndex.getValue().get(RuleDto.StatementKey.LogicalOperator) != null ?
                    LogicalOperator.valueOf(statementIndex.getValue().get(RuleDto.StatementKey.LogicalOperator).toString()).getSpel() : "");
            if(Operator.valueOf(statementIndex.getValue().get(RuleDto.StatementKey.Operator).toString()).equals(Operator.in)) {
                List<String> valuesList = Arrays.stream(statementIndex.getValue().get(RuleDto.StatementKey.Value).toString().split(","))
                        .map(String::trim).collect(Collectors.toList());
                valuesList.forEach(RuleSpelHelper::validateRuleValue);
                String inlineValuesList = valuesList.stream().map(s -> String.format("'%s'", s))
                        .collect(Collectors.joining(","));
                ruleSB.append(String.format("{%s}", inlineValuesList));
                ruleSB.append(String.format(Operator.valueOf(statementIndex.getValue().get(RuleDto.StatementKey.Operator).toString()).getSpel(),
                        statementIndex.getValue().get(RuleDto.StatementKey.Field)));
            } else {
                ruleSB.append(statementIndex.getValue().get(RuleDto.StatementKey.Field));
                validateRuleValue(statementIndex.getValue().get(RuleDto.StatementKey.Value).toString());
                ruleSB.append(String.format(Operator.valueOf(statementIndex.getValue().get(RuleDto.StatementKey.Operator).toString()).getSpel(),
                    statementIndex.getValue().get(RuleDto.StatementKey.Value)));
            }
        }
        return ruleSB.toString();
    }

    private static void validateRuleValue(String value) {
        if(!value.matches(VALUE_REGEX)) {
            throw new RuntimeException("Rule statement value invalid!");
        }
    }

}
