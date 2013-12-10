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

import java.util.Map;
import java.util.Map.Entry;

public interface PersistentSortedMap<K, V> extends PersistentMap<K, V> {

    @Override
    PersistentSortedMap<K, V> assoc(K key, V value);

    @Override
    PersistentSortedMap<K, V> assocAll(Map<? extends K, ? extends V> map);

    @Override
    PersistentSortedMap<K, V> assocAll(Iterable<Map.Entry<K, V>> entries);

    @Override
    PersistentSortedMap<K, V> merge(K key, V value, Merger<Map.Entry<K, V>> merger);
    
    @Override
    PersistentSortedMap<K, V> mergeAll(Map<? extends K, ? extends V> map, Merger<Entry<K, V>> merger);

    @Override
    PersistentSortedMap<K, V> mergeAll(Iterable<Entry<K, V>> entries, Merger<Entry<K, V>> merger);

    @Override
    PersistentSortedMap<K, V> dissoc(Object key);

    @Override
    PersistentSortedMap<K, V> dissoc(Object key, Merger<Entry<K, V>> merger);
    
    @Override
    // TODO: MutableSortedMap
    MutableMap<K, V> toMutableMap();
    
    @Override
    // TODO: SortedMap
    Map<K, V> asMap();
}
