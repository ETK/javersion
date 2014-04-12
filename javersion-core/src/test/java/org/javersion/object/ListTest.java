package org.javersion.object;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.javersion.object.PolymorphismTest.Cat;
import org.javersion.object.PolymorphismTest.Dog;
import org.javersion.object.PolymorphismTest.Pet;
import org.javersion.path.PropertyPath;
import org.junit.Test;

import com.google.common.collect.Lists;

public class ListTest {

    @Versionable
    public static class Owner {
        private List<Pet> pets;
    }

    private ValueTypes valueTypes = ValueTypes.builder()
            .withClass(Pet.class)
            .havingSubClasses(Dog.class, Cat.class)
            .build();
    
    private final ObjectSerializer<Owner> serializer = new ObjectSerializer<>(Owner.class, valueTypes);

    @Test
    public void Write_And_Read_Owner_With_Cats_And_Dogs() {
        Owner owner = new Owner();
        owner.pets = Lists.newArrayList(new Cat("Mirri"), new Dog("Musti"));
        Map<PropertyPath, Object> map = serializer.write(owner);
        
        owner = serializer.read(map);
        assertThat(owner.pets, hasSize(2));
        
        assertThat(owner.pets.get(0), instanceOf(Cat.class));
        assertThat(owner.pets.get(0).name, equalTo("Mirri"));
        assertThat(((Cat) owner.pets.get(0)).meow, equalTo(true));
        
        assertThat(owner.pets.get(1), instanceOf(Dog.class));
        assertThat(owner.pets.get(1).name, equalTo("Musti"));
        assertThat(((Dog) owner.pets.get(1)).bark, equalTo(true));
    }

}
