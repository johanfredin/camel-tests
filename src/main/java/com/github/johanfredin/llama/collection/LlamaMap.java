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
package com.github.johanfredin.llama.collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extension of {@link LinkedHashMap} with the methods
 * of(K1, V1...) available but in this case the maps created using the of(...) methods are
 * not immutable.
 *
 * @param <K> the map key
 * @param <V> the map value
 * @author johan
 */
public class LlamaMap<K, V> extends LinkedHashMap<K, V> {

    /**
     * Create a new empty instance
     */
    public LlamaMap() {}

    /**
     * Create a new instance calling super constructor on the params
     *
     * @param initialCapacity
     * @param loadFactor
     */
    public LlamaMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Create a new instance calling super constructor on the params
     *
     * @param initialCapacity
     */
    public LlamaMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Create a new instance calling super constructor on the params
     *
     * @param m
     */
    public LlamaMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    /**
     * Create a new instance calling super constructor on the params
     *
     * @param initialCapacity
     * @param loadFactor
     * @param accessOrder
     */
    public LlamaMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    /**
     * Create a new mutable map
     *
     * @param k1  key
     * @param v1  value
     * @param <K> the map key type
     * @param <V> the map value type
     * @return a map with the keys and values passed in
     */
    public static <K, V> LlamaMap<K, V> of(K k1, V v1) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        return map;
    }

    /**
     * Create a new map
     *
     * @param k1  the first key
     * @param v1  the first value
     * @param k2  the second key
     * @param v2  the second value
     * @param <K> the map key type
     * @param <V> the map value type
     * @return a map with the keys and values passed in
     */
    public static <K, V> LlamaMap<K, V> of(K k1, V v1, K k2, V v2) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    /**
     * Create a new map
     *
     * @param k1  the first key
     * @param v1  the first value
     * @param k2  the second key
     * @param v2  the second value
     * @param k3  the third key
     * @param v3  the third value
     * @param <K> the map key type
     * @param <V> the map value type
     * @return a map with the keys and values passed in
     */
    public static <K, V> LlamaMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    /**
     * Create a new map
     *
     * @param k1  the first key
     * @param v1  the first value
     * @param k2  the second key
     * @param v2  the second value
     * @param k3  the third key
     * @param v3  the third value
     * @param k4  the fourth key
     * @param v4  the fourth value
     * @param <K> the map key type
     * @param <V> the map value type
     * @return a map with the keys and values passed in
     */
    public static <K, V> LlamaMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    /**
     * Create a new map
     *
     * @param k1  the first key
     * @param v1  the first value
     * @param k2  the second key
     * @param v2  the second value
     * @param k3  the third key
     * @param v3  the third value
     * @param k4  the fourth key
     * @param v4  the fourth value
     * @param k5  the fifth key
     * @param v5  the fifth value
     * @param <K> the map key type
     * @param <V> the map value type
     * @return a map with the keys and values passed in
     */
    public static <K, V> LlamaMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        var map = new LlamaMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
