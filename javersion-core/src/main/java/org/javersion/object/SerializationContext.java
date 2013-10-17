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
package org.javersion.object;

import static java.util.Collections.unmodifiableMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.javersion.path.PropertyPath;

import com.google.common.collect.Maps;

public abstract class SerializationContext<V> {

    private static class QueueItem {
        public final PropertyPath path;
        public final Object object;
        public QueueItem(PropertyPath path, Object object) {
            this.path = path;
            this.object = object;
        }
        
    }
    
    private final ValueMapping<V> rootDescriptor;
    
    private final Map<PropertyPath, V> properties = Maps.newHashMap();

    private final Deque<QueueItem> queue = new ArrayDeque<>();
    
    private QueueItem currentItem;
    
    public SerializationContext(ValueMapping<V> rootDescriptor) {
        this.rootDescriptor = rootDescriptor;
    }
    
    public Object getCurrentObject() {
        return currentItem.object;
    }
    
    public PropertyPath getCurrentPath() {
        return currentItem.path;
    }
    
    public void serialize(Object object) {
        if (currentItem == null) {
            serialize(PropertyPath.ROOT, object);
            run();
        } else {
            throw new IllegalStateException("Serialization already in proggress");
        }
    }
    
    public void serialize(PropertyPath path, Object object) {
        queue.add(new QueueItem(path, object));
    }
    
    public void run() {
        while ((currentItem = queue.pollFirst()) != null) {
            ValueMapping<V> descriptor = rootDescriptor.get(currentItem.path);
            descriptor.valueType.serialize(this);
        }
    }
    
    public void put(V value) {
        put(getCurrentPath(), value);
    }
    
    public void put(PropertyPath path, V value) {
        if (properties.containsKey(path)) {
            throw new IllegalArgumentException("Duplicate value for " + path);
        }
        properties.put(path, value);
    }
    
    public Map<PropertyPath, V> getProperties() {
        return unmodifiableMap(properties);
    }
    
}
