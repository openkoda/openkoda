package com.openkoda.uicomponent;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;


@Component
public class JsParser {

    public List<String> getFunctions(String code){
        List<String> functions = new ArrayList<>();
        for(JsPattern jsPattern : JsPattern.values()){
            addFunctions(jsPattern, code, functions);
        }
        return functions;
    }

    private  void addFunctions(JsPattern jsPattern, String code, List<String> functions) {
        Matcher matcher = jsPattern.getPattern().matcher(code);
        while(matcher.find()){
            functions.add(matcher.group(jsPattern.getNameGroup()).trim() + "(" + matcher.group(jsPattern.getArgsGroup()).trim().replaceAll(" ","") + ")");
        }
    }

    private enum JsPattern {
        function_standard(compile("export\\s+function\\s+([^)]*)\\s*\\(([^)]*)"), 1 ,2),
        function_equals(compile("export\\s+(const|var|let)\\s+([^=]*)\\s*=\\s*function\\s*\\(([^)]*)\\)"), 2 ,3),
        function_lambda(compile("export\\s+(const|var|let)\\s+([^=]*)\\s*=\\s*\\(*([^)]*)\\)*\\s*=>"), 2 ,3);

        final Pattern pattern;
        final Integer nameGroup;
        final Integer argsGroup;

        JsPattern(Pattern pattern, Integer nameGroup, Integer argsGroup) {
            this.pattern = pattern;
            this.nameGroup = nameGroup;
            this.argsGroup = argsGroup;
        }

        Pattern getPattern() {
            return pattern;
        }

        Integer getNameGroup() {
            return nameGroup;
        }

        Integer getArgsGroup() {
            return argsGroup;
        }
    }
}
