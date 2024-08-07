package uk.ac.core.common.util.datastructure;

import java.util.HashMap;

/**
 * The builder for {@link HashMap}.
 */
public final class FluentHashMap<K, V> extends HashMap<K, V> {

    private FluentHashMap() {

    }

    public static <K, V> FluentHashMap<K, V> map(K key, V value) {
        return new FluentHashMap<K, V>().with(key, value);
    }

    public FluentHashMap<K, V> with(K key, V value) {
        put(key, value);
        return this;
    }
}