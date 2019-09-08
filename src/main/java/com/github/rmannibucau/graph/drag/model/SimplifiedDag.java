/**
 * Copyright (C) 2006-2019 rmannibucau
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rmannibucau.graph.drag.model;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.Data;

@Data
public class SimplifiedDag {
    private final Dag delegate;
    private final Collection<Component> nodes;
    private final Collection<SimplifiedDag.Edge> edges;

    public String toGraphviz() { // http://www.webgraphviz.com/
        final AtomicInteger counter = new AtomicInteger();
        final Map<String, String> nodeNames = new HashMap<>();
        return "digraph D {\n" +
                nodes.stream()
                    .map(n -> "  " + nodeNames.computeIfAbsent(n.getId(), k -> "node_" + counter.getAndIncrement()) +
                            " [label=\"id=" + n.getId() + "\\ntype=" + n.getType() + "\"]")
                    .collect(joining("\n", "\n", "\n")) +
                edges.stream()
                    .map(e -> "  " + nodeNames.get(e.getFrom().getNodeId()) + " -> " + nodeNames.get(e.getTo().getNodeId()))
                    .collect(joining("\n", "\n", "\n")) +
                "\n}";
    }

    public Stream<Component> findUpStream(final String nodeIdStart, final Predicate<Component> nodePredicate) {
        return doFindUpStream(nodeIdStart, nodePredicate, new HashSet<>());
    }

    private Stream<Component> doFindUpStream(final String nodeIdStart, final Predicate<Component> nodePredicate,
                                             final Collection<String> ignored) {
        if (!ignored.add(nodeIdStart)) {
            return Stream.empty();
        }
        final List<Component> previousNodes = edges.stream()
                .filter(edge -> Objects.equals(edge.getTo().getNodeId(), nodeIdStart))
                .map(edge -> edge.getFrom().getNodeId())
                .filter(Objects::nonNull)
                .map(nodeId -> nodes.stream().filter(n -> Objects.equals(nodeId, n.getId())).findAny().orElse(null))
                .filter(Objects::nonNull)
                .collect(toList());
        final List<Component> matchingNodes = previousNodes.stream()
                .filter(nodePredicate)
                .collect(toList());
        return Stream.concat(
                matchingNodes.stream(),
                previousNodes.stream()
                        .filter(it -> !matchingNodes.contains(it))
                        .flatMap(node -> doFindUpStream(node.getId(), nodePredicate, ignored)));
    }

    @Data
    public static class Edge {
        private final Dag.Port from;
        private final Dag.Port to;
        private final Dag.Step step;
    }
}
