/*
 * Copyright 2013 Samppa Saarela
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javersion.util;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterators.transform;

import java.util.Iterator;
import java.util.Map;

import org.javersion.util.AbstractHashMap.Entry;

import com.google.common.base.Function;

public abstract class AbstractHashMap<K, V, This extends AbstractHashMap<K, V, This>> extends AbstractHashTrie<K, Entry<K,V>, AbstractHashMap<K, V, This>> implements Iterable<Map.Entry<K, V>> {
    
    @SuppressWarnings("rawtypes")
    private static final Function TO_ENTRY = new Function() {
        @SuppressWarnings("unchecked")
        @Override
        public Object apply(Object input) {
            return toEntry((Map.Entry) input);
        }
    };
    
    @SuppressWarnings("rawtypes")
    private static final Function TO_MAP_ENTRY = new Function() {
        @Override
        public Object apply(Object input) {
            return (Map.Entry) input;
        }
    };
    
    public This assoc(K key, V value) {
        return assoc(new Entry<K, V>(key, value));
    }
    
    public This assoc(java.util.Map.Entry<? extends K, ? extends V> entry) {
        return merge(entry, null);
    }

    public This assocAll(Map<? extends K, ? extends V> map) {
        return mergeAll(map, null);
    }
    
    public This assocAll(Iterable<Map.Entry<K, V>> entries) {
        return mergeAll(entries, null);
    }

    
    public This merge(K key, V value, Merger<Entry<K, V>> merger) {
        return doMerge(new Entry<K, V>(key, value), merger);
    }
    
    public This merge(Map.Entry<? extends K, ? extends V> entry, Merger<Entry<K, V>> merger) {
        return doMerge(toEntry(entry), merger);
    }

    @SuppressWarnings("unchecked")
    protected This doMerge(Entry<K, V> entry, Merger<Entry<K, V>> merger) {
        final UpdateContext<Entry<K, V>> updateContext = updateContext(1, merger);
        try {
            return (This) doAdd(updateContext, toEntry(entry));
        } finally {
            commit(updateContext);
        }
    }

    
    @SuppressWarnings("unchecked")
    public This mergeAll(Map<? extends K, ? extends V> map, Merger<Entry<K, V>> merger) {
        final UpdateContext<Entry<K, V>> updateContext = updateContext(map.size(), merger);
        try {
            return (This) doAddAll(updateContext, transform(map.entrySet().iterator(), TO_ENTRY));
        } finally {
            commit(updateContext);
        }
    }

    @SuppressWarnings("unchecked")
    public This mergeAll(Iterable<Map.Entry<K, V>> entries, Merger<Entry<K, V>> merger) {
        final UpdateContext<Entry<K, V>> updateContext = updateContext(32, merger);
        try {
            return (This) doAddAll(updateContext, transform(entries.iterator(), TO_ENTRY));
        } finally {
            commit(updateContext);
        }
    }
    
    protected UpdateContext<Entry<K, V>> updateContext(int expectedSize, Merger<Entry<K, V>> merger) {
        return new UpdateContext<>(expectedSize, merger);
    }
    
    protected void commit(UpdateContext<Entry<K, V>> updateContext) {
        updateContext.commit();
    }

    
    public This dissoc(Object key) {
        return dissoc(key, null);
    }

    @SuppressWarnings("unchecked")
    public This dissoc(Object key, Merger<Entry<K, V>> merger) {
        final UpdateContext<Entry<K, V>> updateContext = updateContext(1, merger);
        try {
            return (This) doRemove(updateContext, key);
        } finally {
            commit(updateContext);
        }
    }


    public This update(MapUpdate<K, V> updateFunction) {
        return update(32, updateFunction);
    }

    public This update(int expectedUpdates, MapUpdate<K, V> updateFunction) {
        return update(expectedUpdates, updateFunction, null);
    }

    public abstract This update(int expectedUpdates, MapUpdate<K, V> updateFunction, Merger<Entry<K, V>> merger);
    
    
    public V get(Object key) {
        Entry<K, V> entry = root().find(key);
        return entry != null ? entry.getValue() : null;
    }
    
    public boolean containsKey(Object key) {
        return root().find(key) != null;
    }

    @SuppressWarnings("unchecked")
    public Iterator<Map.Entry<K, V>> iterator() {
        return transform(doIterator(), TO_MAP_ENTRY);
    }
    
    
    @SuppressWarnings("unchecked")
    protected static <K, V> Entry<K, V> toEntry(Map.Entry<? extends K, ? extends V> entry) {
        if (entry instanceof Entry) {
            return (Entry<K, V>) entry;
        } else {
            return new Entry<K, V>(entry.getKey(), entry.getValue());
        }
    }
    
    public static final class Entry<K, V> extends AbstractHashTrie.Entry<K, Entry<K, V>> implements Map.Entry<K, V> {
        
        final V value;
        
        public Entry(K key, V value) {
            super(key);
            this.value = value;
        }
        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
     
        public String toString() {
            return "" + key + ": " + value;
        }
        
        @Override
        public EntryEquality equals(Entry<K, V> other) {
            if (other == this) {
                return EntryEquality.EQUAL;
            } else if (equal(key, other.key)) {
                if (equal(value, other.value)) {
                    return EntryEquality.EQUAL;
                } else {
                    return EntryEquality.KEY;
                }
            } else {
                return EntryEquality.NONE;
            }
        }
    }
}