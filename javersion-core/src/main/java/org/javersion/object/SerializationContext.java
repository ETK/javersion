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

import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;

import org.javersion.path.PropertyPath;

import com.google.common.collect.Maps;

public abstract class SerializationContext<V> {

    private final ValueMapping<V> rootMapping;
    
    private final Map<PropertyPath, V> properties = Maps.newHashMap();

    private final Deque<QueueItem<PropertyPath, Object>> queue = new ArrayDeque<>();
    
    private final IdentityHashMap<Object, PropertyPath> objects = Maps.newIdentityHashMap();
    
    private QueueItem<PropertyPath, Object> currentItem;
    
    public SerializationContext(ValueMapping<V> rootMapping) {
        this.rootMapping = rootMapping;
    }
    
//    public Object getCurrentObject() {
//        return currentItem.value;
//    }
    
    public PropertyPath getCurrentPath() {
        return currentItem.key;
    }
    
    public void serialize(Object root) {
        if (currentItem == null) {
            serialize(PropertyPath.ROOT, root);
            run();
        } else {
            throw new IllegalStateException("Serialization already in proggress");
        }
    }
    
    public void serialize(PropertyPath path, Object object) {
        queue.add(new QueueItem<PropertyPath, Object>(path, object));
    }
    
    public boolean isSerialized(Object object) {
        return objects.containsKey(object);
    }
    
    public void run() {
        while ((currentItem = queue.pollFirst()) != null) {
            ValueMapping<V> mapping = getValueMapping(currentItem.key);
            if (!mapping.isReferenceType() 
                    && mapping.hasChildren() 
                    && currentItem.hasValue() 
                    && objects.put(currentItem.value, currentItem.key) != null) {
                illegalReferenceException();
            } 
            mapping.valueType.serialize(currentItem.value, this);
        }
    }
    
    public ValueMapping<V> getValueMapping(PropertyPath path) {
        return rootMapping.get(path);
    }
    
    public ValueType<V> getValueType(PropertyPath path) {
        return getValueMapping(path).valueType;
    }

    private void illegalReferenceException() {
        throw new IllegalArgumentException(format(
                "Multiple references to the same object: \"%s\"@\"%s\"", 
                currentItem.value, 
                currentItem.key));
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

    public ValueMapping<V> getRootMapping() {
        return rootMapping;
    }
    
}
