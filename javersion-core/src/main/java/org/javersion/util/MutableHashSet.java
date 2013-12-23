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



public class MutableHashSet<E> extends AbstractTrieSet<E, MutableHashSet<E>> {
    
    private final Thread owner = Thread.currentThread();
    
    private UpdateContext<Entry<E>> updateContext = new UpdateContext<>(32);
    
    private Node<E, Entry<E>> root;
    
    private int size;

    public MutableHashSet() {
        this(null, 0);
    }
    
    MutableHashSet(Node<E, Entry<E>> root, int size) {
        this.root = root;
        this.size = size;
    }
    
    public PersistentHashSet<E> toPersistentSet() {
        verifyThread();
        updateContext.commit();
        return new PersistentHashSet<>(root, size);
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
    protected MutableHashSet<E> doReturn(Node<E, Entry<E>> newRoot, int newSize) {
        verifyThread();
        root = newRoot;
        size = newSize;
        return null;
    }

    @Override
    protected Node<E, Entry<E>> root() {
        return root;
    }
    
    @Override
    protected UpdateContext<Entry<E>> updateContext(int expectedUpdates, Merger<Entry<E>> merger) {
        verifyThread();
        if (updateContext.isCommitted()) {
            updateContext = new UpdateContext<Entry<E>>(expectedUpdates, merger);
        } else {
            updateContext.validate();
            updateContext.merger(merger);
        }
        return updateContext;
    }
    
    @Override
    protected void commit(UpdateContext<?> updateContext) {
        // Nothing to do here
    }

}