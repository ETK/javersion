package org.javersion.core.simple;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javersion.core.Merge;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class SimpleVersionGraphTest {
    
    
    public static List<VersionExpectation> EXPECTATIONS = Arrays.asList(
            when(version(1l)
                .properties(mapOf(
                            "firstName", "John",
                            "lastName", "Doe")))
                .expectProperties(mapOf(
                            "firstName", "John",
                            "lastName", "Doe")),


            when(version(2l)
                    .parents(setOf(1l))
                    .properties(mapOf(
                                "status", "Single")))
                    .expectProperties(mapOf(
                                "firstName", "John", // 1
                                "lastName", "Doe", // 1
                                "status", "Single")), // 2


            when(version(3l)
                    .parents(setOf(1l))
                    .properties(mapOf(
                                "mood", "Lonely")))
                    .mergeRevisions(setOf(3l, 2l))
                    .expectProperties(mapOf(
                                "firstName", "John", // 1
                                "lastName", "Doe", // 1
                                "status", "Single", // 2
                                "mood", "Lonely")), // 3


            when(version(4l)
                    .parents(setOf(3l))
                    .properties(mapOf(
                                "lastName", "Foe",
                                "status", "Just married",
                                "mood", "Ecstatic",
                                "married", "2013-10-12")))
                    .mergeRevisions(setOf(4l))
                    .expectProperties(mapOf(
                                "firstName", "John", // 1
                                "lastName", "Foe", // 4
                                "status", "Just married", // 4
                                "mood", "Ecstatic", // 4
                                "married", "2013-10-12")), // 4

            then("Merge with ancestor")
                    .mergeRevisions(setOf(3l, 4l))
                    .expectRevisions(setOf(4l))
                    .expectProperties(mapOf(
                            "firstName", "John", // 1
                            "lastName", "Doe", // 1
                            "status", "Just married", // 4 - "Single" from version 2 is not merged here!
                            "mood", "Lonely", // 3
                            "married", "2013-10-12")) // 4
                    .expectConflicts(multimapOf(
                            "lastName", "Foe", // 4
                            "mood", "Ecstatic" // 4
                            )),


            when(version(5l)
                    .parents(setOf(2l))
                    .properties(mapOf(
                                "status", "Just married")))
                    .mergeRevisions(setOf(4l, 5l))
                    .expectProperties(mapOf(
                                "firstName", "John",
                                "lastName", "Foe",
                                "status", "Just married", // 4 and 5 - not conflicting!
                                "mood", "Ecstatic", // 4
                                "married", "2013-10-12")), // 4

            then("Conflicting merge")
                    .mergeRevisions(setOf(5l, 4l))
                    .expectProperties(mapOf(
                            "firstName", "John", // 1
                            "lastName", "Doe", // 1
                            "status", "Just married", // 4 and 5 - not conflicting!
                            "mood", "Ecstatic", // 4
                            "married", "2013-10-12")) // 4.
                    .expectConflicts(multimapOf(
                            "lastName", "Foe" // 4
                            ))
            );
    
    @Test
    public void Sequential_Updates() {
        SimpleVersionGraph versionGraph = null;
        for (VersionExpectation expectation : EXPECTATIONS) {
            if (expectation.version != null) {
                if (versionGraph == null) {
                    versionGraph = SimpleVersionGraph.init(expectation.version);
                } else {
                    versionGraph = versionGraph.commit(expectation.version);
                }
            }
            assertExpectations(versionGraph, expectation);
        }
    }

    private static Multimap<String, String> multimapOf(String... entries) {
        ImmutableMultimap.Builder<String, String> map = ImmutableMultimap.builder();
        for (int i=0; i+1 < entries.length; i+=2) {
            map.put(entries[i], entries[i+1]);
        }
        return map.build();
    }

    @Test
    public void Bulk_Load() {
        SimpleVersionGraph versionGraph = 
                SimpleVersionGraph.init(filter(transform(EXPECTATIONS, getVersion), notNull()));
        
        for (VersionExpectation expectation : EXPECTATIONS) {
            assertExpectations(versionGraph, expectation);
        }
    }
    
    private void assertExpectations(SimpleVersionGraph versionGraph, VersionExpectation expectation) {
        Merge<String, String> merge = versionGraph.merge(expectation.mergeRevisions);
        assertThat(title("revisions", versionGraph, expectation), 
                merge.revisions, 
                equalTo(expectation.expectedRevisions));
        
        assertThat(title("properties", versionGraph, expectation), 
                merge.getProperties(), 
                equalTo(expectation.expectedProperties));
        
        assertThat(title("conflicts", versionGraph, expectation), 
                Multimaps.transformValues(merge.conflicts, merge.getVersionPropertyValue), 
                equalTo(expectation.expectedConflicts));
    }
    
    private static String title(String assertLabel, SimpleVersionGraph graph, VersionExpectation expectation) {
        return assertLabel + " of #" + graph.tip.getRevision() + (expectation.title != null ? ": " + expectation.title : "");
    }

    public static Function<VersionExpectation, SimpleVersion> getVersion = new Function<VersionExpectation, SimpleVersion>() {
        @Override
        public SimpleVersion apply(VersionExpectation input) {
            return input.version;
        }
    };
    
    public static class VersionExpectation {
        public final String title;
        public final SimpleVersion version;
        public Set<Long> mergeRevisions = ImmutableSet.of();
        public Map<String, String> expectedProperties;
        public Multimap<String, String> expectedConflicts = ImmutableMultimap.of();
        public Set<Long> expectedRevisions;
        public VersionExpectation(String title) {
            this(null, title);
        }
        public VersionExpectation(SimpleVersion version) {
            this(version, null);
        }
        public VersionExpectation(SimpleVersion version, String title) {
            this.version = version;
            this.title = title;
            if (version != null) {
                this.mergeRevisions = ImmutableSet.of(version.revision);
            }
            this.expectedRevisions = mergeRevisions;
        }
        public VersionExpectation mergeRevisions(Set<Long> mergeRevisions) {
            this.mergeRevisions = mergeRevisions;
            this.expectedRevisions = mergeRevisions;
            return this;
        }
        public VersionExpectation expectProperties(Map<String, String> expectedProperties) {
            this.expectedProperties = expectedProperties;
            return this;
        }
        public VersionExpectation expectRevisions(Set<Long> expectedRevisions) {
            this.expectedRevisions = expectedRevisions;
            return this;
        }
        public VersionExpectation expectConflicts(Multimap<String, String> expectedConflicts) {
            this.expectedConflicts = expectedConflicts;
            return this;
        }
    }
    
    public static SimpleVersion.Builder version(long rev) {
        return new SimpleVersion.Builder(rev);
    }
    
    public static VersionExpectation when(SimpleVersion.Builder builder) {
        return new VersionExpectation(builder.build());
    }
    public static VersionExpectation then(String title) {
        return new VersionExpectation(title);
    }
    @SafeVarargs
    public static <T> Set<T> setOf(T... revs) {
        return ImmutableSet.copyOf(revs);
    }
    
    public static Map<String, String> mapOf(String... entries) {
        ImmutableMap.Builder<String, String> map = ImmutableMap.builder();
        for (int i=0; i+1 < entries.length; i+=2) {
            map.put(entries[i], entries[i+1]);
        }
        return map.build();
    }
    
}
