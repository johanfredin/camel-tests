package com.github.johanfredin.llama.pojo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Holds a collection of {@link JoinKey} instances and properties for it.
 * New instances can be created using regular constructors or invoke statically
 * @author johan
 */
public class Keys {

    private List<JoinKey> keys;

    /**
     * Create a new empty instance
     */
    public Keys() {}

    /**
     * Create a new instance
     * @param keys the collection of keys to use.
     */
    public Keys(List<JoinKey> keys) {
        setKeys(keys);
    }

    /**
     * Create a new Keys instance based on passed in keys.
     * A new filterValidateAgainst key will be created for each key.
     * Name in main and joining will be the same here.
     *
     * @param keys the keys to create filterValidateAgainst key objects of
     * @return a new keys instance based on passed in parameters
     */
    public static Keys of(String... keys) {
        return new Keys(Arrays
                .stream(keys)
                .map(JoinKey::new)
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Create a new keys instance statically calling {@link #Keys(List)}
     * @param keys the collection of keys to use
     * @return a new instance with keys.
     */
    public static Keys of(JoinKey... keys) {
        return new Keys(Arrays
                .stream(keys)
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Create a new Keys instance based on passed in map.
     * Join key objects will be created for each map entry where
     * map key={@link JoinKey#getKeyInMain()} and value={@link JoinKey#getKeyInJoining()}
     * @param keys the key-value pairs to create filterValidateAgainst key objects of.
     * @return a list of filterValidateAgainst keys based on passed in map.
     */
    public static Keys of(Map<String, String> keys) {
        var keyList = keys.entrySet()
                .stream()
                .map(e -> new JoinKey(e.getKey(), e.getValue()))
                .collect(Collectors.toCollection(LinkedList::new));
        return new Keys(keyList);
    }

    /**
     * @return the collection of keys to use
     */
    public List<JoinKey> getKeys() {
        return keys;
    }

    /**
     * @param keys the collection of keys to use.
     */
    public void setKeys(List<JoinKey> keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "Keys{" +
                "keys=" + keys +
                '}';
    }
}
