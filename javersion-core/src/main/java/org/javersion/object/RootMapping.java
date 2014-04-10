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

import java.util.Map;

import org.javersion.path.PropertyPath;
import org.javersion.util.Check;

import com.google.common.collect.ImmutableMap;

public class RootMapping extends ValueMapping {

    private Map<TypeMappingKey, ValueMapping> typeMappings;
    
    RootMapping(ValueType valueType, Map<TypeMappingKey, ValueMapping> typeMappings) {
        super(valueType);
        this.typeMappings = typeMappings;
    }
    
    public ValueMapping get(TypeMappingKey mappingKey) {
        return typeMappings.get(mappingKey);
    }
    
    public ValueMapping get(PropertyPath path) {
        Check.notNull(path, "path");
        ValueMapping currentMapping = this;
        for (PropertyPath currentPath : path.toSchemaPath().asList()) {
            currentMapping = currentMapping.getChild(currentPath.getName());
            if (currentMapping == null) {
                throw new IllegalArgumentException("Path not found: " + currentPath);
            }
        }
        return currentMapping;
    }

    ValueMapping addPath(PropertyPath path) {
        ValueMapping currentMapping = this;
        for (PropertyPath currentPath : path.toSchemaPath().asList()) {
            String childName = currentPath.getName();
            if (!currentMapping.hasChild(childName)) {
                currentMapping = currentMapping.addChild(childName, new ValueMapping());
            } else {
                currentMapping = currentMapping.getChild(childName);
            }
        }
        return currentMapping;
    }

    @Override
    void lock() {
        super.lock();
        typeMappings = ImmutableMap.copyOf(typeMappings);
    }

}
