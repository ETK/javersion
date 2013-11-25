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


public class PersistentHashSet<E> extends AbstractTrieSet<E, PersistentHashSet<E>> {
    
    private final Node<E, Entry<E>> root;
    
    private final int size;

    public PersistentHashSet() {
        this(null, 0);
    }
    
    @SuppressWarnings("unchecked")
    PersistentHashSet(Node<E, Entry<E>> root, int size) {
        this.root = root != null ? root : EMPTY_NODE;
        this.size = size;
    }

    public MutableHashSet<E> toMutableSet() {
        return new MutableHashSet<E>(root, size);
    }
    
    public ImmutableTrieSet<E> asSet() {
        return new ImmutableTrieSet<E>(this);
    }

    @Override
    public PersistentHashSet<E> update(int expectedUpdates, SetUpdate<E> updateFunction) {
        MutableHashSet<E> mutableSet = toMutableSet();
        updateFunction.apply(mutableSet);
        if (root == mutableSet.root()) {
            return this;
        } else {
            return new PersistentHashSet<>(mutableSet.root(), mutableSet.size());
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    protected PersistentHashSet<E> doReturn(Node<E, Entry<E>> newRoot, int newSize) {
        if (newRoot == root) {
            return this;
        }
        return new PersistentHashSet<>(newRoot, newSize);
    }

    @Override
    protected Node<E, Entry<E>> root() {
        return root;
    }
}
