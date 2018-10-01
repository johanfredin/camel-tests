package se.fredin.llama.processor.join;

import se.fredin.llama.processor.Field;
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
        for (var entry : map.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                // The keys are the same for all maps in the collection so simply returning the first entry is good enough.
                return entry.getValue().get(0).keySet();
            }
        }
        throw new RuntimeException("No map keys could be found in list");
    }

    public static Map<String, String> getFields(Map<String, String> mapToTakeFrom, Fields fields, List<JoinKey> joinKeys) {

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
     * @param joinKeys
     * @return a map containing all the entries from the passed in map (main is the owner when there are duplicate keys)
     */
    public static Map<String, String> createMergedMap(Map<String, String> main, Map<String, String> joining, List<JoinKey> joinKeys) {
        var result = new LinkedHashMap<String, String>();

        // First delete all

        main.entrySet().
                forEach(e -> result.put(e.getKey(), e.getValue()));

        joining.entrySet().
                forEach(e -> {
                    if(!result.containsKey(e.getKey())) {
                        result.put(e.getKey(), e.getValue());
                    }
                });

        return result;
    }

    /**
     * Create a list of join keys based on passed in parameters.
     * A new join key will be created for each key.
     * Name in main and joining will be the same here.
     *
     * @param keys the keys to create join key objects of
     * @return a list of join keys based on passed in parameters
     */
    public static List<JoinKey> joinKeys(String... keys) {
        var joinKeys = new ArrayList<JoinKey>();
        for (var key : keys) {
            joinKeys.add(new JoinKey(key));
        }
        return joinKeys;
    }

    /**
     * Create a list of join keys based on passed in map.
     * Join key objects will be created for each map entry where map key={@link JoinKey#getKeyInMain()} and value={@link JoinKey#getKeyInJoining()}
     *
     * @param keys the key-value pairs to create join key objects of.
     * @return a list of join keys based on passed in map.
     */
    public static List<JoinKey> joinKeys(Map<String, String> keys) {
        var joinKeys = new ArrayList<JoinKey>();
        for (var entry : keys.entrySet()) {
            joinKeys.add(new JoinKey(entry.getKey(), entry.getValue()));
        }
        return joinKeys;
    }

    /**
     * Create a fields object based on passed in parameters.
     * A new field will be created for each key.
     * Name in main and joining will be the same here.
     *
     * @param fields the fields to create a fields objects of
     * @return a fields object based on passed in parameters
     */
    public static Fields createFields(String... fields) {
        var fieldsObject = new Fields();
        for (var key : fields) {
            fieldsObject.addField(new Field(key));
        }
        return fieldsObject;
    }

    /**
     * Create a list of fields based on passed in map.
     * fields objects will be created for each map entry where map key={@link Field#getName()} and value={@link Field#getOutName()}
     *
     * @param fields the key-value pairs to create field objects of.
     * @return a fields object based on passed in map.
     */
    public static Fields createFields(Map<String, String> fields) {
        var fieldsObject = new Fields();
        for (var entry : fields.entrySet()) {
            fieldsObject.addField(new Field(entry.getKey(), entry.getValue()));
        }
        return fieldsObject;
    }
}
