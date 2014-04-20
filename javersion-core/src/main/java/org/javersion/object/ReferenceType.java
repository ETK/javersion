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
import org.javersion.path.PropertyTree;
import org.javersion.util.Check;


public final class ReferenceType implements ValueType, IdentifiableType {
    
    private final IdentifiableType identifiableType;
    
    private final PropertyPath targetRoot;
    
    public ReferenceType(IdentifiableType identifiableType, PropertyPath targetRoot) {
        this.identifiableType = Check.notNull(identifiableType, "keyType");
        this.targetRoot = Check.notNull(targetRoot, "targetRoot");
    }
    
    @Override
    public void serialize(Object object, WriteContext context) {
        String id = identifiableType.toString(object);
        context.put(id);
        context.serialize(targetRoot.index(id), object);
    }

    @Override
    public Object instantiate(PropertyTree propertyTree, Object value, ReadContext context) throws Exception {
        String id = (String) value;
        return context.getObject(targetRoot.index(id));
    }

    @Override
    public void bind(PropertyTree propertyTree, Object object, ReadContext context) throws Exception {}

    @Override
    public Class<String> getTargetType() {
        return String.class;
    }

    @Override
    public String toString(Object object) {
        return identifiableType.toString(object);
    }

}
