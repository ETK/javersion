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

public class MutableMap<K, V> extends AbstractTrieMap<K, V, MutableMap<K, V>> {
    
    private final Thread owner = Thread.currentThread();
    
    private ContextReference<K, V>  contextReference;
    
    private Node<K, V> root;
    
    private int size;
    
    @SuppressWarnings("unchecked")
    public MutableMap() {
        this(EMPTY_NODE, 0);
    }

    MutableMap(Node<K, V> root, int size) {
        this(new ContextReference<K, V>(new UpdateContext<K, V>(32, null)), root, size);
    }

    MutableMap(ContextReference<K, V>  context, Node<K, V> root, int size) {
        this.contextReference = context;
        this.root = root;
        this.size = size;
    }

    @Override
    Node<K, V> getRoot() {
        verifyThread();
        return root;
    }

    @Override
    protected MutableMap<K, V> self() {
        return this;
    }
    
    public PersistentMap<K, V> toPersistentMap() {
        verifyThread();
        contextReference.commit();
        return new PersistentMap<K, V>(root, size);
    }
    
    private void verifyThread() {
        if (owner != Thread.currentThread()) {
            throw new IllegalStateException("MutableMap should only be accessed form the thread it was created in.");
        }
    }

    @Override
    protected ContextReference<K, V>  contextReference(int expectedUpdates, Merger<K, V> merger) {
        verifyThread();
        if (contextReference.isCommitted()) {
            contextReference = new ContextReference<K, V>(new UpdateContext<K, V>(expectedUpdates, merger));
        } else {
            contextReference.validate();
            UpdateContext<K, V> context = contextReference.get();
            context.merger = merger;
        }
        return contextReference;
    }

    @Override
    public int size() {
        verifyThread();
        return size;
    }

    @Override
    public MutableMap<K, V> update(int expectedUpdates, MapUpdate<K, V> updateFunction, Merger<K, V> merger) {
        verifyThread();
        updateFunction.apply(this);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MutableMap<K, V> doReturn(ContextReference<K, V> context, Node<K, V> newRoot, int newSize) {
        this.root = (Node<K, V>) (newRoot == null ? EMPTY_NODE : newRoot);
        this.size = newSize;
        return this;
    }

}