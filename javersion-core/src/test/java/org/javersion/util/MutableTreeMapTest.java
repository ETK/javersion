package org.javersion.util;

public class MutableTreeMapTest extends AbstractMutableMapTest {

    @Override
    protected MutableMap<Integer, Integer> emptyMap() {
        return new MutableTreeMap<Integer, Integer>();
    }

    @Override
    void assertMapProperties(MutableMap<Integer, Integer> map) {
        PersistentTreeMapTest.assertNodeProperties(((MutableTreeMap<Integer, Integer>) map).root());
    }

}
