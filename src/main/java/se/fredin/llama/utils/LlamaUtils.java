package se.fredin.llama.utils;

import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.pojo.Field;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility interface with only default methods used throughout the llama api.
 * Holds helper methods for creating collections of llama beans of specific types
 * from passed in exchanges etc...
 *
 * @author johan
 */
public interface LlamaUtils {

    /**
     * Lazy boolean method (and best name ever for a method).
     *
     * @param conditions conditions to check
     * @return true if any of the conditions are true
     */
    default boolean isTrueAny(boolean... conditions) {
        return isTrue(Operator.OR, conditions);
    }

    /**
     * Lazy boolean method (and best name ever for a method).
     *
     * @param conditions conditions to check
     * @return true if all of the conditions are true
     */
    default boolean isTrueAll(boolean... conditions) {
        return isTrue(Operator.AND, conditions);
    }

    /**
     * Lazy boolean method (and best name ever for a method).
     *
     * @param operator   whether all or any of the conditions should be true
     * @param conditions conditions to check
     * @return true if operator=all and all conditions are true or operator=or and any of the conditions are true.
     */
    default boolean isTrue(Operator operator, boolean... conditions) {
        var trueStatementFound = false;
        for (var condition : conditions) {
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


    /**
     * Create a new list of {@link LlamaBean}s from the body of the exchange
     *
     * @param e   the exchange with the body we want
     * @param <T> the type of the list (must extend {@link LlamaBean}
     * @return a new list of type T from the body of the exchange
     */
    @SuppressWarnings("unchecked")
    default <T extends LlamaBean> List<T> asLlamaBeanList(Exchange e) {
        return new ArrayList<T>(e.getIn().getBody(List.class));
    }

    /**
     * Create a list containing maps of key/value=string from the body
     * of the passed in exchange.
     *
     * @param e the exchange containing the body we want
     * @return a list containing maps of key/value=string from the body
     * of the passed in exchange.
     */
    @SuppressWarnings("unchecked")
    default List<Map<String, String>> asListOfMaps(Exchange e) {
        return new ArrayList<Map<String, String>>(e.getIn().getBody(List.class));
    }

    /**
     * Create a {@link LinkedList} containing maps of key/value=string from the body
     * of the passed in exchange.
     *
     * @param e the exchange containing the body we want
     * @return a linked list containing maps of key/value=string from the body
     * of the passed in exchange.
     */
    @SuppressWarnings("unchecked")
    default List<Map<String, String>> asLinkedListOfMaps(Exchange e) {
        return new LinkedList<Map<String, String>>(e.getIn().getBody(List.class));
    }

    /**
     * Create a list of type T from given exchange body.
     * @param e the exchange with the body we want
     * @param <T> the type of the list
     * @return a list of type T from given exchange body.
     */
    @SuppressWarnings("unchecked")
    default <T> List<T> asTypedList(Exchange e) {
        return new ArrayList<T>(e.getIn().getBody(List.class));
    }

    /**
     * Create a map of type key=K, value=V from body of passed in exchange
     * @param e the exchange we want the body of.
     * @param <K> the key type of the map.
     * @param <V> the value type of the map.
     * @return a map of type key=K, value=V from body of passed in exchange
     */
    default <K, V> Map<K, V> asTypedMap(Exchange e) {
        return new HashMap<K, V>(e.getIn().getBody(Map.class));
    }

    /**
     * Creates a map where key = {@link LlamaBean#getId()} and value a List of llama beans.
     *
     * @param e   the exchange that is assumed to have a list of {@link LlamaBean} instances
     * @param <T> the Type of the bean (must extend Llamabean
     * @return a map of llama beans grouped by bean id.
     */
    @SuppressWarnings("unchecked")
    default <T extends LlamaBean> Map<Serializable, List<T>> asLlamaBeanMap(Exchange e) {
        List<T> list = asLlamaBeanList(e);
        return list
                .stream()
                .collect(Collectors.groupingBy(T::getId));
    }

    /**
     * Replace a part of the URL with another if the inURL contains that part.
     * Comparison is not case sensitive and potential backslashes (\\) are replaced
     * by forward slash before comparison. So suck it windows!
     * @param inUrl
     * @param inUrlPrefix
     * @param outputUrlPrefix
     * @return
     */
    default String getTransformedUrl(String inUrl, String inUrlPrefix, String outputUrlPrefix) {
        var url = inUrlPrefix.toLowerCase().replace("\\", "/");
        inUrl = inUrl.toLowerCase().replace("\\", "/");
        var outUrl = outputUrlPrefix.toLowerCase().replace("\\", "/");

        return inUrl.replace(url, outUrl);
    }

    default JacksonXMLDataFormat toXml(boolean prettyPrint) {
        var dataFormat = new JacksonXMLDataFormat();
        dataFormat.setPrettyPrint(prettyPrint);
        return dataFormat;
    }


    default <K, V> Map<K, V> getMergedMap(Map<K, V>... maps) {
        return getMergedMap(Arrays.asList(maps), false);
    }

    default <K, V> Map<K, V> getMergedMap(Collection<Map<K, V>> maps, boolean overrideDuplicates) {
        var result = new HashMap<K, V>();
        for (var mapToAdd : maps) {
            if (overrideDuplicates) {
                mapToAdd.forEach((key, value) -> {
                    if (!result.containsKey(key)) {
                        result.put(key, value);
                    }
                });
            } else {
                result.putAll(mapToAdd);
            }
        }
        return result;
    }

    default List<Field> fields(Field... fields) {
        return Arrays.asList(fields);
    }

    default Field field(String name) {
        return field(name, name);
    }

    default Field field(String name, String outName) {
        return new Field(name, outName);
    }

    default boolean withinRange(Object o, int min, int max) {
        return Integer.parseInt(o.toString()) > min && Integer.parseInt(o.toString()) < max;
    }
}
