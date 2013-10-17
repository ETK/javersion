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

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.javersion.object.PropertyPath.ROOT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.javersion.object.PropertyPath;
import org.javersion.object.PropertyPath.Index;
import org.javersion.object.PropertyPath.SubPath;
import org.junit.Ignore;
import org.junit.Test;

public class PropertyPathTest {

    @Test
    public void Path_Equals() {
        assertThat(children_0_name(), equalTo(children_0_name()));
        assertThat(children_0(), equalTo(children_0()));
        assertThat(children, equalTo(children));
        assertThat(ROOT, equalTo(ROOT));
        
        assertThat(children_0(), not(equalTo(children_0_name())));
        assertThat(children_0_name(), not(equalTo(children_0())));
        assertThat(parents_0_name, not(equalTo(parents_1_name)));
        assertThat(list_0, not(equalTo(list_1)));
        assertThat(children_0_name(), not(equalTo(parents_0_name)));
    }
    
    @Test
    public void Hash_Code() {
        HashSet<PropertyPath> paths = newHashSet(
                ROOT,
                parents,
                children_0(),
                children_0_name()
                );
        assertThat(paths, equalTo(newHashSet(
                ROOT,
                parents,
                children_0(),
                children_0_name()
                )));

        assertThat(paths, not(hasItem(parents_0_name)));
        assertThat(paths, not(hasItem(children)));
    }

    @Test
    public void To_String() {
        assertThat(list_0.toString(), equalTo("list[0]"));
        assertThat(children_0().toString(), equalTo("children[0]"));
        assertThat(children_0_name().toString(), equalTo("children[0].name"));
    }
    
    @Test
    public void Nested_Indexes() {
        assertThat(list_1_0.toString(), equalTo("list[1][0]"));
    }
    
    @Test
    public void Peculiar_Index() {
        PropertyPath path = ROOT.property("list").index("index containing \\ [ ] .");
        assertThat(path.toString(),  equalTo("list[index containing \\\\ \\[ \\] \\.]"));
        
        assertThat(PropertyPath.parse(path.toString()), equalTo(path));
    }
    
    @Test
    public void Peculiar_Property() {
        PropertyPath path = ROOT.property("property containing \\ [ ] .");
        assertThat(path.toString(), equalTo("property containing \\\\ \\[ \\] \\."));
        
        assertThat(PropertyPath.parse(path.toString()), equalTo(path));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Parse_Illegal_Start_1() {
        PropertyPath.parse("[index]");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Parse_Illegal_Start_2() {
        PropertyPath.parse(".property");
    }
    
    @Test(expected=IllegalArgumentException.class)
    @Ignore("Implement real parser!")
    public void Parse_Illegal_Index_1() {
        System.out.println(PropertyPath.parse("list[[index]]"));
    }
    
    @Test
    public void Starts_With() {
        assertTrue(list_1.startsWith(ROOT));
        assertTrue(children_0_name().startsWith(children));
        assertTrue(children_0_name().startsWith(ROOT));
        assertTrue(children.startsWith(children));
        
        assertFalse(children.startsWith(children_0_name()));
        assertFalse(ROOT.startsWith(list_0));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Empty_Property_Name() {
        ROOT.property("");
    }
    
    @Test
    public void Schema_Path() {
        assertThat(children_0_name().toSchemaPath().toString(), equalTo("children[].name"));
        assertThat(list_0.toSchemaPath(), equalTo(list_1.toSchemaPath()));
        assertThat(children_0().toSchemaPath(), not(equalTo(parents_0.toSchemaPath())));
        
        PropertyPath emptyIndex = ROOT.property("list").index("");
        assertThat(emptyIndex.toSchemaPath(), sameInstance(emptyIndex));
    }
    
    public static SubPath list_0 = ROOT.property("list").index("0");
    
    public static SubPath list_1 = ROOT.property("list").index("1");
    
    public static SubPath list_1_0 = list_1.index("0");

    public static SubPath children = ROOT.property("children");

    public static SubPath parents = ROOT.property("parents");

    private static final Index parents_0 = parents.index(0);
    
    public static SubPath parents_0_name = parents_0.property("name");
    
    public static SubPath parents_1_name = parents.index(1).property("name");
    
    public static SubPath children_0() {
        return children.index("0");
    }

    public static SubPath children_0_name() {
        return children_0().property("name");
    }

}
