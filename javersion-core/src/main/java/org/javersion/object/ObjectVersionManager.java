package org.javersion.object;

import static com.google.common.collect.ImmutableSet.of;
import static java.util.Arrays.asList;

import java.util.Set;

import org.javersion.core.Merge;
import org.javersion.path.PropertyPath;

public class ObjectVersionManager<O, M> {

    private ObjectVersionGraph<M> versionGraph;

    private Set<Long> heads = of();

    private final ObjectSerializer<O> serializer;

    public ObjectVersionManager(Class<O> clazz) {
        this(new ObjectSerializer<>(clazz));
    }

    public ObjectVersionManager(ObjectSerializer<O> serializer) {
        this.serializer = serializer;
    }

    public ObjectVersionManager<O, M> init() {
        this.versionGraph = ObjectVersionGraph.init();
        return this;
    }

    public ObjectVersionManager<O, M> init(Iterable<ObjectVersion<M>> versions) {
        this.versionGraph = ObjectVersionGraph.init(versions);
        return this;
    }

    public ObjectVersionBuilder<M> buildVersion(O object) {
        long id = versionGraph.isEmpty() ? 1l : versionGraph.getTip().getRevision() + 1;
        ObjectVersionBuilder<M> builder = new ObjectVersionBuilder<M>(this, serializer.write(object), id);
        builder.parents(heads);
        return builder;
    }

    public MergeObject<O> mergeObject(String... branches) {
        return mergeObject(asList(branches));
    }

    public MergeObject<O> mergeObject(Iterable<String> branches) {
        Merge<PropertyPath, Object> merge = versionGraph.mergeBranches(branches);
        MergeObject<O> mergeObject = new MergeObject<>(toObject(merge), merge.getMergeHeads(), merge.getConflicts());
        heads = mergeObject.revisions;
        return mergeObject;
    }

    private O toObject(Merge<PropertyPath, Object> merge) {
        return serializer.read(merge.getProperties());
    }

    Merge<PropertyPath, Object> mergeRevisions(Iterable<Long> revisions) {
        return versionGraph.mergeRevisions(revisions);
    }

    void commit(ObjectVersion<M> version) {
        versionGraph = versionGraph.commit(version);
        heads = of(version.revision);
    }

}
