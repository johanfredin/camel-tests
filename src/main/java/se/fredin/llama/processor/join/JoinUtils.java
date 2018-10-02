package se.fredin.llama.processor.join;

import se.fredin.llama.processor.Fields;
import se.fredin.llama.processor.Keys;

import java.util.*;
import java.util.stream.Collectors;

public class JoinUtils {

    public static final byte EXCHANGE_MAIN = 0;
    public static final byte EXCHANGE_JOINING = 1;

    public static Map<String, List<Map<String, String>>> groupCollection(Keys joinKey, List<Map<String, String>> list) {
        return groupCollection(joinKey, EXCHANGE_MAIN, list);
    }

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

    public static String keysAsString(Map<String, String> mapWithKeyValues, Keys joinKeys, byte exchange) {
        var keyBuilder = new StringBuilder();
        for (var joinKey : joinKeys.getKeys()) {
            keyBuilder.append(mapWithKeyValues.get(exchange == JoinUtils.EXCHANGE_MAIN ? joinKey.getKeyInMain() : joinKey.getKeyInJoining()));
        }
        return keyBuilder.toString();
    }

    public static Set<String> fetchHeader(Map<String, List<Map<String, String>>> map) {
        for (var entry : map.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                // The keys are the same for all maps in the collection so simply returning the first entry is good enough.
                return entry.getValue().get(0).keySet();
            }
        }
        throw new RuntimeException("No map keys could be found in list");
    }

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
        return Map.of();
    }

    public static Map<String, String> createDummyMap(Set<String> mapHeaders, Fields fields) {
        var dummyMap = new HashMap<String, String>();

        // Add main fields
        if (fields.isAllFields()) {
            for (var field : mapHeaders) {
                dummyMap.put(field, "");
            }
        }

        if (fields.hasFields()) {
            for (var mainField : fields.getFields()) {
                dummyMap.put(mainField.getOutName(), "");
            }
        }
        return dummyMap;
    }

    /**
     * Similar to {@link se.fredin.llama.utils.LlamaUtils#getMergedMap(Map[])} except that in this case
     * the map entries are ordered to come out the same way they are added and the main map param will be
     * the first one added to the merged map. Any keys that exists in the joining map that also exist in the
     * main map will be ignored.
     * @param main the main map.
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
