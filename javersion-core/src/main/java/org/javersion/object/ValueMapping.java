package org.javersion.object;

import java.util.Iterator;
import java.util.Map;

import org.javersion.util.Check;

import com.google.common.collect.ImmutableMap;

public final class ValueMapping<V> {
    
    public final ValueType<V> valueType;
    
    public Map<String, ValueMapping<V>> children;

    public ValueMapping(ValueType<V> valueType) {
        this.valueType = Check.notNull(valueType, "valueType");
    }

    public ValueMapping<V> getChild(String name) {
        return children.get(name);
    }
    
    public ValueMapping<V> get(PropertyPath path) {
        Check.notNull(path, "path");
        Iterator<PropertyPath> iter = path.toSchemaPath().iterator();
        PropertyPath parent = iter.next(); // Root
        ValueMapping<V> result = this; // Root
        while (iter.hasNext()) {
            parent = iter.next();
            result = result.getChild(parent.getName());
            if (result == null) {
                throw new IllegalArgumentException("Path not found: " + parent);
            }
        }
        return result;
    }
    
    public Map<String, ValueMapping<V>> getChildren() {
        if (children == null) {
            throw new IllegalStateException("ValueMapping build in proggress");
        }
        return children;
    }
    
    void lock(Map<String, ValueMapping<V>> children) {
        if (this.children != null) {
            throw new IllegalStateException("ValueMapping locked already");
        }
        this.children = ImmutableMap.copyOf(children);
    }
    
}
