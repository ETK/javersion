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

import org.javersion.path.PropertyTree;

public class BasicValueType implements ValueType, IdentifiableType {

    private final Class<?> valueType;
    
    public BasicValueType(Class<?> valueType) {
        this.valueType = valueType;
    }
    
    @Override
    public Object instantiate(PropertyTree propertyTree, Object value, ReadContext context) throws Exception {
        return value;
    }

    @Override
    public void bind(PropertyTree propertyTree, Object object, ReadContext context) throws Exception {}

    @Override
    public void serialize(Object object, WriteContext context) {
        context.put(object);
    }

    @Override
    public Class<?> getTargetType() {
        return valueType;
    }

    @Override
    public String toString(Object object) {
        return object.toString();
    }

}
