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

package com.openkoda.dto;

import java.util.*;
import java.util.stream.Collectors;

public class RuleDto {

    public RuleDto() {
        ifStatements.computeIfAbsent(0L, k -> new TreeMap<>());
        thenStatements.computeIfAbsent(0L, k -> new TreeMap<>());
        elseStatements.computeIfAbsent(0L, k -> new TreeMap<>());
    }

    public enum  StatementKey {
        LogicalOperator, Field, Operator, Value
    }

    public Map<Long, Map<StatementKey, Object>> ifStatements = new TreeMap<>();
    public Map<Long, Map<StatementKey, Object>> thenStatements = new TreeMap<>();
    public Map<Long, Map<StatementKey, Object>> elseStatements = new TreeMap<>();

    public Map<Long, Map<StatementKey, Object>> getIfStatements() {
        return ifStatements;
    }

    public void setIfStatements(Map<Long, Map<StatementKey, Object>> ifStatements) {
        this.ifStatements = ifStatements;
    }

    public Map<Long, Map<StatementKey, Object>> getThenStatements() {
        return thenStatements;
    }

    public void setThenStatements(Map<Long, Map<StatementKey, Object>> thenStatements) {
        this.thenStatements = thenStatements;
    }

    public Map<Long, Map<StatementKey, Object>> getElseStatements() {
        return elseStatements;
    }

    public void setElseStatements(Map<Long, Map<StatementKey, Object>> elseStatements) {
        this.elseStatements = elseStatements;
    }

    //TODO Rule 5.5: DTO should not have code
    public Set<String> allValues() {
        Set<String> allValues = new HashSet<>();
        allValues.addAll(splitValues(ifStatements));
        allValues.addAll(splitValues(thenStatements));
        allValues.addAll(splitValues(elseStatements));
        return allValues;
    }
    //TODO Rule 5.5: DTO should not have code
    private Set<String> splitValues(Map<Long, Map<StatementKey, Object>> statements) {
        Set<String> values = new HashSet();
        extractValuesLists(statements).forEach(strings -> values.addAll(strings));
        return values;
    }

    private Set<List<String>> extractValuesLists(Map<Long, Map<StatementKey, Object>> statements) {
        return statements.values().stream()
                .map(statementKeyObjectMap -> statementKeyObjectMap.get(StatementKey.Value))
                .filter(Objects::nonNull)
                .map(o -> o.toString().split(","))
                .map(Arrays::asList)
                .collect(Collectors.toSet());
    }
}
