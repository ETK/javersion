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

import java.util.Set;

import org.javersion.object.DescribeContext;
import org.javersion.object.LocalTypeDescriptor;
import org.javersion.object.types.IdentifiableType;
import org.javersion.object.types.SetType;
import org.javersion.object.types.ValueType;
import org.javersion.path.PropertyPath;
import org.javersion.reflect.TypeDescriptor;

public class SetTypeMapping implements TypeMapping {

    @Override
    public boolean applies(PropertyPath path, LocalTypeDescriptor localTypeDescriptor) {
        return localTypeDescriptor.typeDescriptor.equalTo(Set.class);
    }

    @Override
    public ValueType describe(PropertyPath path, TypeDescriptor setType, DescribeContext context) {
        TypeDescriptor elementType = setType.resolveGenericParameter(Set.class, 0);
        ValueType valueType = context.describeComponent(path.index(""), setType, elementType);
        return new SetType((IdentifiableType) valueType);
    }

}
