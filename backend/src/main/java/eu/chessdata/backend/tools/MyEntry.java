package eu.chessdata.backend.tools;

import java.util.Map;

/**
 * Created by bogda on 29/11/2015.
 */
public class MyEntry<K, V> implements Map.Entry {
    private final K key;
    private final V value;

    public MyEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Object setValue(Object value) {
        return new IllegalStateException("You should not try to set the value " +
                "Just create a new MyEntry");
    }
}
