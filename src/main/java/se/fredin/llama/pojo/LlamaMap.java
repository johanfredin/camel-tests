package se.fredin.llama.pojo;

import java.util.LinkedHashMap;

/**
 * Extension of {@link LinkedHashMap} with the methods
 * of(K1, V1...) available for ease of use.
 * @param <K> the map key
 * @param <V> the map value
 * @author johan
 */
public class LlamaMap<K, V> extends LinkedHashMap<K, V> {

    public static <K, V> LlamaMap<K, V> of(K k1, V v1) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        return map;
    }

    public static <K, V> LlamaMap<K, V> of(K k1, V v1, K k2, V v2) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> LlamaMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> LlamaMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> LlamaMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }
}
