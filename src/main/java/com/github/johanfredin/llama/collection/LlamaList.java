package com.github.johanfredin.llama.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LlamaList<T> extends LinkedList<T> {

    public LlamaList() {}

    public LlamaList(Collection<? extends T> c) {
        super(c);
    }

    public static<T> LlamaList of(Collection<T>... lists) {
        var result = new LlamaList<T>();
        Arrays.stream(lists).forEach(result::addAll);
        return result;
    }

    public static<T> LlamaList of(T... types) {
        return new LlamaList<T>(List.of(types));
    }
}
