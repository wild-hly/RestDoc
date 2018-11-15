package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.ITypeNameParser;
import lombok.Data;
import lombok.var;

import java.lang.reflect.Type;
import java.util.*;

public class TypeNameParser implements ITypeNameParser {

    private Map<String, Type> _typeNameToType = new HashMap<>();

    @Override
    public String parse(Type type) {
        List<String> items = new ArrayList<>();
        splitTypeName(type.getTypeName(), items);

        var builder = new StringBuilder();
        for (var item : items) {
            item = simpleName(item);
            builder.append(item);
        }

        var simpledName = builder.toString();
        return selectName(type, items, simpledName, 1);
    }

    private String selectName(Type type, List<String> items, String typeName, int count) {

        if (!_typeNameToType.containsKey(typeName)) {
            _typeNameToType.put(typeName, type);
            return typeName;
        }
        if (_typeNameToType.get(typeName).equals(type))
            return typeName;
        else {
            var complexName = complexName(items, count);
            return selectName(type, items, complexName, count++);
        }
    }

    private String complexName(List<String> items, int complexity) {
        var extraClassCount = items.stream().filter(o -> !o.startsWith("java.") && !o.startsWith("javax.")).mapToInt(o -> {
            return getDotCount(o);
        }).sum();
        int javaCount = 0;
        int updateCount = 0;
        for (int i = 0; i < items.size(); ++i) {
            var item = items.get(i);
            if ((item.startsWith("java.") || item.startsWith("javax.")) && (javaCount < (complexity - extraClassCount))) {
                {
                    var simpleResult = simpleName(item, complexity);
                    items.set(i, simpleResult.getResult());
                    updateCount += simpleResult.getConsumeComplexity();
                    javaCount += simpleResult.getConsumeComplexity();
                }
            } else if (updateCount < complexity) {

                var simpleResult = simpleName(item, complexity);
                items.set(i, simpleResult.getResult());
                updateCount += simpleResult.getConsumeComplexity();
            } else {
                items.set(i, simpleName(item));
            }
        }
        var builder = new StringBuilder();
        for (var item : items) {
            builder.append(item);
        }
        return builder.toString();
    }

    private SimpleResult simpleName(String name, int complexity)
    {
        var simpleResult = new SimpleResult();
        var dotCount = getDotCount(name);
        if (dotCount < complexity) {
            simpleResult.setResult(name);
            simpleResult.setConsumeComplexity(dotCount);
            return simpleResult;
        }
        var index = indexOf(name, dotCount - complexity);
        var result = name.substring(index + 1);

        simpleResult.setResult(result);
        simpleResult.setConsumeComplexity(complexity);

        return simpleResult;
    }

    @Data
    public static class SimpleResult
    {
        private String _result;
        private int _consumeComplexity;
    }

    private int getDotCount(String name) {
        return name.length() - name.replaceAll("\\.", "").length();
    }

    private int indexOf(String str, int count)
    {
        int currentCount = 0;
        for (int i = 0; i < str.length(); ++i)
        {
            if (str.charAt(i) == '.')
            {
                currentCount++;
                if (currentCount == count)
                    return i;
            }
        }
        return str.length() - 1;
    }

    private String simpleName(String item) {
        var index = item.lastIndexOf(".");
        return item.substring(index + 1);
    }

    private void splitTypeName(String typeName, List<String> items) {
        int leftAngleBracketIndex = typeName.indexOf('<');
        int rightAngleBracketIndex = typeName.lastIndexOf('>');

        if (leftAngleBracketIndex == -1 || rightAngleBracketIndex == -1) {
            addItem(items, typeName);
            return;
        }
        String rawType = typeName.substring(0, leftAngleBracketIndex);
        addItem(items, rawType);
        items.add("<");
        String typeArgument = typeName.substring(leftAngleBracketIndex + 1, rightAngleBracketIndex);
        splitTypeName(typeArgument, items);
        items.add(">");
    }

    private void addItem(List<String> items, String typeName) {
        var index = typeName.indexOf(',');
        if (index == -1) {
            items.add(typeName);
            return;
        }
        var types = typeName.split(",");
        for (int i = 0; i < types.length; ++i) {
            items.add(types[i]);
            if (i != types.length - 1)
                items.add(",");
        }
    }
}
