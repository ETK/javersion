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
package org.javersion.core.object;

import org.javersion.object.PropertyTree;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.javersion.object.PropertyPath.ROOT;
import static org.junit.Assert.assertThat;
import static org.javersion.core.object.PropertyPathTest.*;

public class PropertyTreeTest {

    @Test
    public void Single_Path() {
        PropertyTree tree = PropertyTree.build(children_0_name());
        assertPropertyTree(tree, 1);
        
        tree = assertPropertyTree(tree, "children", 1);
        tree = assertPropertyTree(tree, "0", 1);
        tree = assertPropertyTree(tree, "name", 0);
    }
    
    private PropertyTree assertPropertyTree(PropertyTree tree, String node, int expectedChildren) {
        tree = tree.get(node);
        assertPropertyTree(tree, expectedChildren);
        return tree;
    }
    
    private void assertPropertyTree(PropertyTree tree, int expectedChildren) {
        assertThat(tree, notNullValue());
        assertThat(tree.getChildrenMap().size(), equalTo(expectedChildren));
    }
    
    @Test
    public void No_Paths() {
        PropertyTree tree = PropertyTree.build();
        assertThat(tree, nullValue());
        
    }

    @Test
    public void Paths() {
        PropertyTree root = PropertyTree.build(
                children_0_name(),
                children_0(),
                ROOT.property("name"), 
                children.index(123));
        
        assertPropertyTree(root, 2);
        
        assertPropertyTree(root, "name", 0);
        
        PropertyTree children = assertPropertyTree(root, "children", 2);

        PropertyTree tree = assertPropertyTree(children, "0", 1);
        tree = assertPropertyTree(tree, "name", 0);
        
        assertPropertyTree(children, "123", 0);
    }
    
}
