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
package org.javersion.core;

import static com.google.common.collect.Iterables.transform;

import org.javersion.util.Check;
import org.javersion.util.MutableSortedMap;
import org.javersion.util.MutableTreeMap;

import com.google.common.base.Function;

public abstract class AbstractVersionGraphBuilder<K, 
                               V, 
                               T extends Version<K, V>, 
                               G extends AbstractVersionGraph<K, V, T, G, B>,
                               B extends AbstractVersionGraphBuilder<K, V, T, G, B>> {

	MutableSortedMap<BranchAndRevision, VersionNode<K, V, T>> heads;
	
    MutableSortedMap<Long, VersionNode<K, V, T>> versionNodes;

    private Function<Long, VersionNode<K, V, T>> revisionToVersionNode = new Function<Long, VersionNode<K, V, T>>() {
        @Override
        public VersionNode<K, V, T> apply(Long input) {
            return getVersionNode(Check.notNull(input, "input"));
        }
    };

    
    protected AbstractVersionGraphBuilder() {
    	reset();
    }
    
    protected AbstractVersionGraphBuilder(G parentGraph) {
    	this.versionNodes = parentGraph.versionNodes.toMutableMap();
        this.heads = parentGraph.heads.toMutableMap();
    }
    
    private void reset() {
    	this.versionNodes = new MutableTreeMap<>();
    	this.heads = new MutableTreeMap<>();
    }
    
    public final void add(T version) {
        Check.notNull(version, "version");
        if (version.type == VersionType.ROOT) {
        	reset();
        }
        Iterable<VersionNode<K, V, T>> parents = revisionsToNodes(version.parentRevisions);
        VersionNode<K, V, T> versionNode = new VersionNode<K, V, T>(version, parents);
        for (VersionNode<K, V, T> parent : versionNode.parents) {
        	if (parent.version.branch.equals(versionNode.version.branch)) {
        		heads.remove(new BranchAndRevision(parent));
        	}
        }
        heads.put(new BranchAndRevision(versionNode), versionNode);
        versionNodes.put(version.revision, versionNode);
    }
    
    Iterable<VersionNode<K, V, T>> revisionsToNodes(Iterable<Long> revisions) {
        return transform(revisions, revisionToVersionNode);
    }

    private VersionNode<K, V, T> getVersionNode(long revision) {
        VersionNode<K, V, T> node = versionNodes.get(revision);
        if (node == null) {
            throw new VersionNotFoundException(revision);
        }
        return node;
    }

    protected abstract G build();

}