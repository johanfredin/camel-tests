package com.github.johanfredin.llama.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Extension of {@link LinkedList} with some static utility methods to fast create a collection
 * out of several collections.
 * @param <T> what type the collection should consist of.
 * @author johan fredin
 */
public class LlamaList<T> extends LinkedList<T> {

    /**
     * Create a new empty instance
     */
    public LlamaList() {}

    /**
     * Create a new instance
     * calls super with the param
     * @param c an initial collection.
     */
    public LlamaList(Collection<? extends T> c) {
        super(c);
    }

    /**
     * Create a new list of passed in collections.
     * Each collection is added using {@link java.util.Collections#addAll(Collection, Object[])}
     * @param lists the lists to merge into one
     * @param <T> the type of data in the lists
     * @return a merged list
     */
    public static<T> LlamaList of(Collection<T>... lists) {
        var result = new LlamaList<T>();
        Arrays.stream(lists).forEach(result::addAll);
        return result;
    }

    /**
     * Returns a new list with the passed in types
     * @param types the objects to give to the list
     * @param <T> the type of the objects
     * @return a list with the passed in types.
     */
    public static<T> LlamaList of(T... types) {
        return new LlamaList<T>(List.of(types));
    }
}
