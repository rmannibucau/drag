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
package com.github.rmannibucau.graph.drag.model.mapper;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;

import com.github.rmannibucau.graph.drag.model.Dag;
import com.github.rmannibucau.graph.drag.model.SimplifiedDag;

public class Simplyfier {
    public SimplifiedDag simplify(final Dag dag) {
        return new SimplifiedDag(dag, dag.getComponents(), findEdges(dag));
    }

    private Collection<SimplifiedDag.Edge> findEdges(final Dag dag) {
        final Map<String, Dag.Port> indexedPorts = dag.getPorts().stream()
                .collect(toMap(Dag.Port::getId, identity()));
        return dag.getSteps().stream()
                .filter(step -> step.getSourceId() != null && step.getTargetId() != null)
                .filter(step -> indexedPorts.containsKey(step.getSourceId()) && indexedPorts.containsKey(step.getTargetId()))
                .map(step -> new SimplifiedDag.Edge(indexedPorts.get(step.getSourceId()), indexedPorts.get(step.getTargetId()), step))
                .collect(toList());
    }
}
