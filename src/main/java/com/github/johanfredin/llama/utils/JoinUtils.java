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

import com.github.johanfredin.llama.pojo.Fields;
import com.github.johanfredin.llama.pojo.Keys;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class with only public static methods and some final properties
 * that is used with the {@link com.github.johanfredin.llama.processor.generic.JoinCollectionsProcessor}
 *
 * @author johan
 */
public class JoinUtils {

    public static final byte EXCHANGE_MAIN = 0;
    public static final byte EXCHANGE_JOINING = 1;

    /**
     * Group a collection of maps into a map of collection of maps, where map key=the fields in collection specified
     * to be join keys. This makes joining exchanges easier since we can gather all the values into groups and
     * easier find matches.
     *
     * @param joinKeys the fields in the list that are keys.
     * @param list     the list of maps we want to create a map of.
     * @return passed in list wrapped into a map of lists.
     */
    public static Map<String, List<Map<String, String>>> groupCollection(Keys joinKeys, List<Map<String, String>> list) {
        return groupCollection(joinKeys, EXCHANGE_MAIN, list);
    }

    /**
     * Group a collection of maps into a map of collection of maps, where map key=the fields in collection specified
     * to be join keys. This makes joining exchanges easier since we can gather all the values into groups and
     * easier find matches.
     *
     * @param joinKeys the fields in the list that are keys.
     * @param exchange whether or not passed in list belongs to the main or joining exchange (public static is {@link #EXCHANGE_MAIN}
     * @param list     the list of maps we want to create a map of.
     * @return passed in list wrapped into a map of lists.
     */
    public static Map<String, List<Map<String, String>>> groupCollection(Keys joinKeys, byte exchange, List<Map<String, String>> list) {
        var mapOfLists = new HashMap<String, List<Map<String, String>>>();
        for (var map : list) {

            // First set the key
            var key = keysAsString(map, joinKeys, exchange);

            // Now group
            var value = mapOfLists.get(key);
            if (value == null) {
                value = new ArrayList<>();
            }

            // Add new entry
            value.add(map);

            // Update mapOfLists
            mapOfLists.put(key, value);
        }

        return mapOfLists;
    }

    /**
     * Takes all the values from the fields in the passed in map that are join keys and concatenates them
     * into a string.
     *
     * @param mapWithKeyValues the map that holds fields that are keys.
     * @param joinKeys         the keys
     * @param exchange         whether we are passing in the body of the main or joining exchange.
     * @return all the key vales in passed in map as a concatenated string.
     */
    public static String keysAsString(Map<String, String> mapWithKeyValues, Keys joinKeys, byte exchange) {
        var keyBuilder = new StringBuilder();
        for (var joinKey : joinKeys.getKeys()) {
            keyBuilder.append(mapWithKeyValues.get(exchange == JoinUtils.EXCHANGE_MAIN ? joinKey.getKeyInMain() : joinKey.getKeyInJoining()));
        }
        return keyBuilder.toString();
    }

    /**
     * Fetches the key set of first available entry in the passed in map.
     * Every entry in the map will have the same keys.
     *
     * @param map the map that holds the keys.
     * @return the key set of first available map.
     */
    public static Set<String> fetchHeader(Map<String, List<Map<String, String>>> map) {
        for (var entry : map.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                // The keys are the same for all maps in the collection so simply returning the first entry is good enough.
                return entry.getValue().get(0).keySet();
            }
        }
        throw new RuntimeException("No map keys could be found in list");
    }

    /**
     * Helper method for fetching the fields we want from the given map. If we specified
     * we want all fields then the map passed in is returned as is. Otherwise we will fetch
     * the fields in the map matching passed in fields and return those.
     *
     * @param mapToTakeFrom the map that contains the fields we want.
     * @param fields        the fields we want from the map and the potential new output names.
     * @return a map with the fields from the passed in map that we want.
     */
    public static Map<String, String> getFields(Map<String, String> mapToTakeFrom, Fields fields) {
        // Add main fields
        if (fields.isAllFields()) {
            return mapToTakeFrom;
        }

        if (fields.hasFields()) {
            var map = new HashMap<String, String>();
            for (var mainField : fields.getFields()) {
                map.put(mainField.getOutName(), mapToTakeFrom.get(mainField.getName()));
            }
            return map;
        }
        return new HashMap<>();
    }

    /**
     * Creates a "dummy" map where values are empty strings. Used with left/right joins when we have
     * no match and want to apply empty values in joining collections for given id. When we have a
     * specific selection of fields then the out name of that field will be used (if any)
     * @param mapHeaders the key set to create empty string values to
     * @param fields the fields we want from the map.
     * @return a map with empty values.
     */
    public static Map<String, String> createDummyMap(Set<String> mapHeaders, Fields fields) {
        var dummyMap = new HashMap<String, String>();

        // Add main fields
        if (fields.isAllFields()) {
            dummyMap = mapHeaders
                    .stream()
                    .collect(Collectors.toMap(
                            field -> field, field -> "", (a, b) -> b, HashMap::new));
        }

        if (fields.hasFields()) {
            for (var mainField : fields.getFields()) {
                dummyMap.put(mainField.getOutName(), "");
            }
        }
        return dummyMap;
    }

    /**
     * Similar to {@link com.github.johanfredin.llama.utils.LlamaUtils#getMergedMap(Map[])} except that in this case
     * the map entries are ordered to come out the same way they are added and the main map param will be
     * the first one added to the merged map. Any keys that exists in the joining map that also exist in the
     * main map will be ignored.
     *
     * @param main    the main map.
     * @param joining the map with the entries we want to add to the main map.
     * @return a map containing all the entries from the passed in map (main is the owner when there are duplicate keys)
     */
    public static Map<String, String> createMergedMap(Map<String, String> main, Map<String, String> joining) {
        var result = main.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

        joining.entrySet()
                .stream()
                .filter(e -> !result.containsKey(e.getKey()))
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }


}
