/*
 * Copyright 2018 Johan Fredin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.johanfredin.llama.utils;

import com.github.johanfredin.llama.bean.LlamaBean;
import com.github.johanfredin.llama.collection.LlamaMap;
import org.apache.camel.Exchange;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility interface with only public static methods used throughout the llama api.
 * Holds helper methods for creating collections of llama beans of specific types
 * from passed in exchanges etc...
 *
 * @author johan
 */
public class LlamaUtils {

    /**
     * Lazy boolean method (and best name ever for a method).
     *
     * @param conditions conditions to check
     * @return true if any of the conditions are true
     */
    public static boolean isTrueAny(boolean... conditions) {
        return isTrue("||", conditions);
    }

    /**
     * Lazy boolean method (and best name ever for a method).
     *
     * @param conditions conditions to check
     * @return true if all of the conditions are true
     */
    public static boolean isTrueAll(boolean... conditions) {
        return isTrue("&&", conditions);
    }

    /**
     * Lazy boolean method (and best name ever for a method).
     *
     * @param operator   whether all or any of the conditions should be true
     * @param conditions conditions to check
     * @return true if operator=&amp;&amp; and all conditions are true or operator=&#124;&#124; and any of the conditions are true.
     */
    public static boolean isTrue(String operator, boolean... conditions) {
        var trueStatementFound = false;
        for (var condition : conditions) {
            if (!condition) {
                if (operator.equals("&&")) {
                    return false;
                }
            } else {
                trueStatementFound = true;
            }
        }
        return trueStatementFound;
    }


    /**
     * Create a new list of {@link LlamaBean}s from the body of the exchange
     *
     * @param e   the exchange with the body we want
     * @param <T> the type of the list (must extend {@link LlamaBean}
     * @return a new list of type T from the body of the exchange
     */
    @SuppressWarnings("unchecked")
    public static <T extends LlamaBean> List<T> asLlamaBeanList(Exchange e) {
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
    public static List<Map<String, String>> asListOfMaps(Exchange e) {
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
    public static List<Map<String, String>> asLinkedListOfMaps(Exchange e) {
        return new LinkedList<Map<String, String>>(e.getIn().getBody(List.class));
    }

    /**
     * Create a list of type T from given exchange body.
     *
     * @param e   the exchange with the body we want
     * @param <T> the type of the list
     * @return a list of type T from given exchange body.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> asTypedList(Exchange e) {
        return new ArrayList<T>(e.getIn().getBody(List.class));
    }

    /**
     * Create a map of type key=K, value=V from body of passed in exchange
     *
     * @param e   the exchange we want the body of.
     * @param <K> the key type of the map.
     * @param <V> the value type of the map.
     * @return a map of type key=K, value=V from body of passed in exchange
     */
    public static <K, V> Map<K, V> asTypedMap(Exchange e) {
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
    public static <T extends LlamaBean> Map<Serializable, List<T>> asLlamaBeanMap(Exchange e) {
        List<T> list = asLlamaBeanList(e);
        return list
                .stream()
                .collect(Collectors.groupingBy(T::getId));
    }

    /**
     * Replace a part of the URL with another if the inURL contains that part.
     * Comparison is not case sensitive and potential backslashes (\\) are replaced
     * by forward slash before comparison. So suck it windows!
     *
     * @param inUrl           the url in its original form
     * @param inUrlPrefix     the prefix we want to replace
     * @param outputUrlPrefix the prefix we want to replace the inUrlPrefix with
     * @return the url transformed if found prefix exists.
     */
    public static String getTransformedUrl(String inUrl, String inUrlPrefix, String outputUrlPrefix) {
        var url = inUrlPrefix.toLowerCase().replace("\\", "/");
        inUrl = inUrl.toLowerCase().replace("\\", "/");
        var outUrl = outputUrlPrefix.toLowerCase().replace("\\", "/");

        return inUrl.replace(url, outUrl);
    }

    /**
     * Takes an arbitrary amount of maps and merges them all into one.
     *
     * @param maps arbitrary amount of maps of type (K, V)
     * @param <K>  key type
     * @param <V>  value type
     * @return a map containing all passed in maps
     */
    public static <K, V> Map<K, V> getMergedMap(Map<K, V>... maps) {
        return getMergedMap(List.of(maps), true);
    }

    /**
     * Takes an arbitrary amount of maps and merges them all into one.
     *
     * @param maps               arbitrary amount of maps of type (K, V)
     * @param overrideDuplicates if true then any any key already placed in the map
     *                           will not be overwritten (default is true).
     * @param <K>                key type
     * @param <V>                value type
     * @return a map containing all passed in maps
     */
    public static <K, V> Map<K, V> getMergedMap(Collection<Map<K, V>> maps, boolean overrideDuplicates) {
        var result = new HashMap<K, V>();
        for (var mapToAdd : maps) {
            if (!overrideDuplicates) {
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

    /**
     * Helper to check if an integer value is within the allowed within the given range
     *
     * @param o   the object that we should be able to parse to an integer
     * @param min the value the integer must be larger than.
     * @param max the value the integer must be smaller than.
     * @return true if passed in value is within the given range
     */
    public static boolean withinRange(Object o, int min, int max) {
        return Integer.parseInt(o.toString()) > min && Integer.parseInt(o.toString()) < max;
    }

    /**
     * Check if the passed in map qualifies as a header.
     * @param potentialHeader the map to check whether is a header or not
     * @return true if each key/values are identical for all map entries.
     */
    public static boolean isHeader(Map<String, String> potentialHeader) {
        return potentialHeader.entrySet()
                .stream()
                .allMatch(me -> me.getKey().equals(me.getValue()));
    }

    /**
     * When we are working with lists of maps between exchanges then the header record gets lost.
     * If we want it included in the result we can "re-attach" it to the body of the exchange
     * by simply adding a new entry at the first index containing the keyset. When we later marshal this
     * into a CSV file or similar format the header row will be returned.
     * @param body the collection to insert the header to.
     * @return the passed in collection + the header.
     */
    public static Map<String, String> reconnectHeader(List<Map<String, String>> body) {
        return body.get(0).keySet()
                .stream()
                .collect(Collectors.toMap(key -> key, key -> key, (a, b) -> b, LlamaMap::new));
    }

    /**
     * When we are working with lists of maps between exchanges then the header record gets lost.
     * If we want it included in the result we can "re-attach" it to the body of the exchange
     * by simply adding a new entry at the first index containing the keyset. When we later marshal this
     * into a CSV file or similar format the header row will be returned.
     * @param exchange the exchange assumed to contain a body of format List of Maps.
     * @return the exchange with an updated body containing the body + the header
     * @throws RuntimeException if the body of passed in exchange is not a List of Maps.
     */
    public static Exchange reconnectHeader(Exchange exchange) {
        if(exchange.getIn().getBody() instanceof List) {
            var body = asLinkedListOfMaps(exchange);
            body.add(0, reconnectHeader(body));
            exchange.getIn().setBody(body);
        }
        throw new RuntimeException("Can not add header row to exchange when body is not a List of Maps. Body of passed in exchange=" + exchange.getIn().getBody().getClass());
    }

}
