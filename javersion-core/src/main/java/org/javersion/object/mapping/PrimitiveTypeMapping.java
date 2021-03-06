/*
 * Copyright 2014 Samppa Saarela
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
package org.javersion.object.mapping;

import org.javersion.object.LocalTypeDescriptor;
import org.javersion.path.PropertyPath;
import org.javersion.reflect.TypeDescriptor;

public class PrimitiveTypeMapping extends SimpleTypeMapping {
    
    private final Class<?> primitiveType;

    public PrimitiveTypeMapping(Class<?> wrapperType, Class<?> primitiveType) {
        super(wrapperType);
        this.primitiveType = primitiveType;
    }

    @Override
    public boolean applies(PropertyPath path, LocalTypeDescriptor localTypeDescriptor) {
        TypeDescriptor typeDescriptor = localTypeDescriptor.typeDescriptor;
        return super.applies(path, localTypeDescriptor) || typeDescriptor.getRawType().equals(primitiveType);
    }

}