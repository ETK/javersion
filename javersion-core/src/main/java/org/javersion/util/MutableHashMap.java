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
package org.javersion.util;


public class MutableHashMap<K, V> extends AbstractHashMap<K, V, MutableHashMap<K, V>> {
    
    private final Thread owner = Thread.currentThread();
    
    private UpdateContext<Entry<K, V>>  updateContext;
    
    private Node<K, Entry<K, V>> root;
    
    private int size;
    
    @SuppressWarnings("unchecked")
    public MutableHashMap() {
        this(EMPTY_NODE, 0);
    }

    MutableHashMap(Node<K, Entry<K, V>> root, int size) {
        this(new UpdateContext<Entry<K, V>>(32, null), root, size);
    }

    MutableHashMap(UpdateContext<Entry<K, V>>  context, Node<K, Entry<K, V>> root, int size) {
        this.updateContext = context;
        this.root = root;
        this.size = size;
    }

    @Override
    protected Node<K, Entry<K, V>> root() {
        verifyThread();
        return root;
    }

    @Override
    protected MutableHashMap<K, V> self() {
        return this;
    }
    
    public PersistentHashMap<K, V> toPersistentMap() {
        verifyThread();
        updateContext.commit();
        return PersistentHashMap.create(root, size);
    }
    
    private void verifyThread() {
        if (owner != Thread.currentThread()) {
            throw new IllegalStateException("MutableMap should only be accessed form the thread it was created in.");
        }
    }

    @Override
    public int size() {
        verifyThread();
        return size;
    }

    @Override
    public MutableHashMap<K, V> update(int expectedUpdates, MapUpdate<K, V> updateFunction, Merger<Entry<K, V>> merger) {
        verifyThread();
        updateFunction.apply(this);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MutableHashMap<K, V> doReturn(Node<K, Entry<K, V>> newRoot, int newSize) {
        this.root = (Node<K, Entry<K, V>>) (newRoot == null ? EMPTY_NODE : newRoot);
        this.size = newSize;
        return this;
    }
    
    @Override
    protected UpdateContext<Entry<K, V>> updateContext(int expectedUpdates, Merger<Entry<K, V>> merger) {
        verifyThread();
        if (updateContext.isCommitted()) {
            updateContext = new UpdateContext<Entry<K, V>>(expectedUpdates, merger);
        } else {
            updateContext.validate();
            updateContext.merger(merger);
        }
        return updateContext;
    }
    
    @Override
    protected void commit(UpdateContext<Entry<K, V>> updateContext) {
        // Nothing to do here
    }

}