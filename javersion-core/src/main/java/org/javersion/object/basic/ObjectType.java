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
package org.javersion.object.basic;

import java.util.Set;

import org.javersion.object.AbstractObjectType;
import org.javersion.reflect.TypeDescriptor;

public class ObjectType extends AbstractObjectType<Object> {
    
    public ObjectType(Set<TypeDescriptor> types) {
        super(types);
    }

    @Override
    public Object toValue(Object object) {
        return object.getClass();
    }

    @Override
    protected Object fromValue(Object value) throws Exception {
        return ((Class<?>) value).newInstance();
    }

}
