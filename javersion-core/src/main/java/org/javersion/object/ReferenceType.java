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

import org.javersion.path.PropertyPath;
import org.javersion.reflect.FieldDescriptor;
import org.javersion.util.Check;


public final class ReferenceType<V> implements IndexableType<V> {

    private final FieldDescriptor idField;
    
    private final ValueMappingKey idMappingKey;
    
    private final PropertyPath targetRoot;
    
    public ReferenceType(FieldDescriptor idField) {
        this.idField = Check.notNull(idField, "idField");
        this.idMappingKey = new ValueMappingKey(idField);
        this.targetRoot = AbstractEntityTypeFactory.getTargetRoot(idField);
    }
    
    public String toString(Object object, RootMapping<V> rootMapping) {
        return getIdType(rootMapping).toString(idField.get(object), rootMapping);
    }
    
    private IndexableType<V> getIdType(RootMapping<V> rootMapping) {
        return (IndexableType<V>) rootMapping.get(idMappingKey).valueType;
    }
    
    @Override
    public void serialize(Object object, SerializationContext<V> context) {
        PropertyPath path = context.getCurrentPath();
        if (object == null) {
            context.put(path, null);
        } else {
            Object idValue = idField.get(object);
            IndexableType<V> idType = getIdType(context.getRootMapping());
            idType.serialize(idValue, context);
            if (!context.containsKey(targetRoot)) {
                String id = idType.toString(idValue, context.getRootMapping());
                context.serialize(targetRoot.index(id), object);
            }
        }
    }

}
