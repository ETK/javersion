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

import java.util.List;

import org.javersion.object.ValueType;
import org.javersion.object.ValueTypes;
import org.javersion.reflect.TypeDescriptors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class BasicValueTypes extends ValueTypes<Object> {
    
    private static List<ValueType<Object>> defaultTypes(TypeDescriptors typeDescriptors) {
        return ImmutableList.<ValueType<Object>>of(
            new BasicEntityType(typeDescriptors),
            new PrimitivesType()
            );
    };

    @SafeVarargs
    public BasicValueTypes(ValueType<Object>... types) {
        this(TypeDescriptors.DEFAULT, types);
    }

    public BasicValueTypes(Iterable<ValueType<Object>> types) {
        this(TypeDescriptors.DEFAULT, types);
    }

    @SafeVarargs
    public BasicValueTypes(TypeDescriptors typeDescriptors, ValueType<Object>... types) {
        this(typeDescriptors, ImmutableList.copyOf(types));
    }
    
    public BasicValueTypes(TypeDescriptors typeDescriptors, Iterable<ValueType<Object>> types) {
        super(Iterables.concat(types, defaultTypes(typeDescriptors)));
    }

}
