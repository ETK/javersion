/*
 *  Copyright 2013 Samppa Saarela
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

import static com.google.common.collect.Iterables.transform;
import static org.javersion.util.AbstractRedBlackTree.Color.RED;

import java.util.Comparator;
import java.util.Iterator;

import org.javersion.util.PersistentTreeSet.Node;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class PersistentTreeSet<E> extends AbstractRedBlackTree<E, Node<E>, PersistentTreeSet<E>> {
    
    @SuppressWarnings("rawtypes")
    private static final PersistentTreeSet EMPTY = new PersistentTreeSet();
    
    @SuppressWarnings("rawtypes")
    private static final Function GET_ELEMENT = new Function() {
        @Override
        public Object apply(Object input) {
            return ((Node)input).getKey();
        }
    };

    @SuppressWarnings("unchecked")
    public static <E> PersistentTreeSet<E> empty() {
        return EMPTY;
    }
    
    public static <E> PersistentTreeSet<E> empty(Comparator<? super E> comparator) {
        return new PersistentTreeSet<E>(comparator);
    }
    
    
    private final Node<E> root;

    private final int size;
    
    private PersistentTreeSet() {
        super();
        root = null;
        size = 0;
    }

    private PersistentTreeSet(Comparator<? super E> comparator) {
        super(comparator);
        root = null;
        size = 0;
    }

    private PersistentTreeSet(Comparator<? super E> comparator, Node<E> root, int size) {
        super(comparator);
        this.root = root;
        this.size = size;
    }

    public int size() {
        return size;
    }

    public boolean contains(E key) {
        return find(root, key) != null;
    }
    
    Node<E> root() {
        return root;
    }

    public PersistentTreeSet<E> conj(E value) {
        UpdateContext<Node<E>> context = new UpdateContext<Node<E>>(1);
        return doAdd(context, root, new Node<E>(context, value, RED));
    }

    public PersistentTreeSet<E> conjAll(Iterable<E> coll) {
        final UpdateContext<Node<E>> context = new UpdateContext<Node<E>>(32);
        return doAddAll(context, root, transform(coll, new EntryToNode<E>(context)));
    }

    public PersistentTreeSet<E> disj(Object keyObj) {
        return doRemove(new UpdateContext<Node<E>>(1), root, keyObj);
    }

    @SuppressWarnings("unchecked")
    public Iterator<E> iterator() {
        return Iterators.transform(doIterator(root, true), GET_ELEMENT);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected PersistentTreeSet<E> doReturn(Comparator<? super E> comparator, Node<E> newRoot, int newSize) {
        if (newRoot == root) {
            return this;
        } else if (newRoot == null) {
            return EMPTY;
        }
        return new PersistentTreeSet<E>(comparator, newRoot, newSize);
    }

    
    public String toString() {
        return root == null ? "NIL" : root.toString();
    }

    private static final class EntryToNode<E> implements Function<E, Node<E>> {
        private final UpdateContext<Node<E>> context;

        private EntryToNode(UpdateContext<Node<E>> context) {
            this.context = context;
        }

        @Override
        public Node<E> apply(E input) {
            return new Node<E>(context, input, RED);
        }
    }

    static class Node<E> extends AbstractRedBlackTree.Node<E, Node<E>> {

        public Node(UpdateContext<? super Node<E>> context, E key, Color color) {
            this(context, key, color, null, null);
        }
        
        public Node(UpdateContext<? super Node<E>> context, E key, Color color, Node<E> left, Node<E> right) {
            super(context, key, color, left, right);
        }
        
        public E getKey() {
            return key;
        }
        
        @Override
        public Node<E> self() {
            return this;
        }

        @Override
        protected Node<E> cloneWith(UpdateContext<? super Node<E>> currentContext) {
            return new Node<E>(currentContext, key, color, left, right);
        }

        @Override
        protected Node<E> replaceWith(UpdateContext<? super Node<E>> currentContext, Node<E> node) {
            return this;
        }
    }
}
