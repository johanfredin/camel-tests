package se.fredin.llama.processor.join;

import se.fredin.llama.processor.Fields;

import java.util.*;

public class JoinUtils {

    public static final byte EXCHANGE_MAIN = 0;
    public static final byte EXCHANGE_JOINING = 1;

    public static Map<String, List<Map<String, String>>> groupCollection(JoinKey joinKey, List<Map<String, String>> list) {
        return groupCollection(List.of(joinKey), EXCHANGE_MAIN, list);
    }

    public static Map<String, List<Map<String, String>>> groupCollection(JoinKey joinKey, byte exchange, List<Map<String, String>> list) {
        return groupCollection(List.of(joinKey), exchange, list);
    }

    public static Map<String, List<Map<String, String>>> groupCollection(List<JoinKey> joinKeys, byte exchange, List<Map<String, String>> list) {
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

    public static String keysAsString(Map<String, String> mapWithKeyValues, List<JoinKey> joinKeys, byte exchange) {
        var keyBuilder = new StringBuilder();
        for (var joinKey : joinKeys) {
            keyBuilder.append(mapWithKeyValues.get(exchange == JoinUtils.EXCHANGE_MAIN ? joinKey.getKeyInMain() : joinKey.getKeyInJoining()));
        }
        return keyBuilder.toString();
    }

    public static Set<String> fetchHeader(Map<String, List<Map<String, String>>> map) {
        for(var entry : map.entrySet()) {
            if(entry.getValue() != null && !entry.getValue().isEmpty()) {
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
        return null;
    }

    public static Map<String, String> createDummyMap(Set<String> mapHeaders, Fields fields) {
        var dummyMap = new HashMap<String, String>();

        // Add main fields
        if (fields.isAllFields()) {
            for(var field : mapHeaders) {
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
}
