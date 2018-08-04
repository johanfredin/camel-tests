package se.fredin.llama.utils;

import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.Field;

import java.util.*;
import java.util.stream.Collectors;

public class ProcessorUtils {

    public static boolean isTrueAny(boolean... conditions) {
        return isTrue(Operator.OR, conditions);
    }

    public static boolean isTrueAll(boolean... conditions) {
        return isTrue(Operator.AND, conditions);
    }

    public static boolean isTrue(Operator operator, boolean... conditions) {
        boolean trueStatementFound = false;
        for (boolean condition : conditions) {
            if (!condition) {
                if (operator == Operator.AND) {
                    return false;
                }
            }
            trueStatementFound = true;
        }

        if (operator == Operator.OR) {
            return trueStatementFound;
        }
        return true;
    }


    @SuppressWarnings("unchecked")
    public static <T extends LlamaBean> List<T> asFxkBeanList(Exchange e) {
        return new ArrayList<T>(e.getIn().getBody(List.class));
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, String>> asList(Exchange e) {
        return new ArrayList<Map<String, String>>(e.getIn().getBody(List.class));
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> asTypedList(Exchange e) {
        return new ArrayList<T>(e.getIn().getBody(List.class));
    }

    @SuppressWarnings("unchecked")
    public static <T extends LlamaBean> Map<Object, List<T>> asMap(Exchange e) {
        return ProcessorUtils.<T>asFxkBeanList(e)
                .stream()
                .collect(Collectors.groupingBy(T::getId));
    }

    public static String getTransformedUrl(String immUrl, String immUrlPrefix, String outputUrlPrefix) {
        String url = immUrlPrefix.toLowerCase().replace("\\", "/");
        immUrl = immUrl.toLowerCase().replace("\\", "/");
        String outUrl = outputUrlPrefix.toLowerCase().replace("\\", "/");

        return immUrl.replace(url, outUrl);
    }

    public static JacksonXMLDataFormat toXml(boolean prettyPrint) {
        JacksonXMLDataFormat dataFormat = new JacksonXMLDataFormat();
        dataFormat.setPrettyPrint(prettyPrint);
        return dataFormat;
    }


    @SafeVarargs
    public static<K, V> Map<K, V> getMergedMap(Map<K, V>... maps) {
        Map<K, V> map = new HashMap<>();
        for(Map<K, V> mapToAdd : maps) {
            if(mapToAdd != null) {
                map.putAll(mapToAdd);
            }
        }
        return map;
    }

    public static List<Field> fields(Field... fields) {
        return Arrays.asList(fields);
    }

    public static Field field(String name) {
        return field(name, name);
    }

    public static Field field(String name, String outName) {
        return new Field(name, outName);
    }

    public static boolean withinRange(Object o, int min, int max) {
        return Integer.parseInt(o.toString()) > min && Integer.parseInt(o.toString()) < max;
    }
}