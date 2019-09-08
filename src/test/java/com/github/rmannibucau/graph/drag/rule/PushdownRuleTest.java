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
package com.github.rmannibucau.graph.drag.rule;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.json.Json;

import org.junit.jupiter.api.Test;
import com.github.rmannibucau.graph.drag.model.Component;
import com.github.rmannibucau.graph.drag.model.Dag;

class PushdownRuleTest {
    @Test
    void noop() {
        final Dag source = newDag();
        final Dag output = new PushdownRule().apply(source);
        assertEquals(source, output);
    }

    @Test
    void pushdown() {
        final Dag source = newDag();
        final Dag output = doPushdown(source);
        assertNotSame(source, output);
        assertNotEquals(source, output);

        assertEquals(source.getPorts(), output.getPorts());
        assertEquals(source.getSteps(), output.getSteps());
        assertEquals(
            source.getComponents().stream().filter(it -> !"tfm".equals(it.getId())).map(Component::getId).collect(toList()),
            output.getComponents().stream().filter(it -> !"tfm".equals(it.getId())).map(Component::getId).collect(toList()));

        final Component noop = output.getComponents().get(1);
        assertNotEquals(source.getComponents().get(1), noop);
        assertTrue(noop.getData().isEmpty());
        assertEquals("noop", noop.getType());
        assertEquals("tfm", noop.getId());

        final Component pushedDown = output.getComponents().get(0);
        assertEquals("{\"$pushdown\":[{\"$from\":\"t\",\"$configuration\":{}}]}", pushedDown.getData().toString());
    }

    @Test
    void pushdownAndMergeSourceConfig() {
        final Dag source = newDag();
        source.getComponents().get(0).setData(Json.createObjectBuilder().add("original", 1).build());

        final Dag output = doPushdown(source);

        final Component pushedDown = output.getComponents().get(0);
        assertEquals("{\"original\":1,\"$pushdown\":[{\"$from\":\"t\",\"$configuration\":{}}]}", pushedDown.getData().toString());
    }

    @Test
    void pushdownAndMergeSourceAndPushdownConfig() {
        final Dag source = newDag();
        source.getComponents().get(0).setData(Json.createObjectBuilder()
                .add("original", 1)
                .add("$pushdown", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder().add("comp", "set"))
                        .build())
                .build());

        final Dag output = doPushdown(source);

        final Component pushedDown = output.getComponents().get(0);
        assertEquals(
            "{\"original\":1,\"$pushdown\":[{\"comp\":\"set\"},{\"$from\":\"t\",\"$configuration\":{}}]}",
            pushedDown.getData().toString());
    }

    private Dag doPushdown(final Dag source) {
        return new PushdownRule(
                new PushdownRule.Configuration(
                        "noop",
                        singletonMap("t", new PushdownRule.PushdownComponent(singletonList("s")))))
                .apply(source);
    }

    private Dag newDag() {
        final Component sourceComponent = new Component();
        sourceComponent.setId("src");
        sourceComponent.setType("s");

        final Component transformComponent = new Component();
        transformComponent.setId("tfm");
        transformComponent.setType("t");

        final Component sinkComponent = new Component();
        sinkComponent.setId("sink");
        sinkComponent.setType("k");

        final Dag.Port p1 = new Dag.Port();
        p1.setNodeId(sourceComponent.getId());
        p1.setId("p1");

        final Dag.Port p2 = new Dag.Port();
        p2.setNodeId(transformComponent.getId());
        p2.setId("p2");

        final Dag.Port p3 = new Dag.Port();
        p3.setNodeId(transformComponent.getId());
        p3.setId("p3");

        final Dag.Port p4 = new Dag.Port();
        p4.setNodeId(sourceComponent.getId());
        p4.setId("p4");

        final Dag.Step s1 = new Dag.Step();
        s1.setId("s1");
        s1.setSourceId(p1.getId());
        s1.setTargetId(p2.getId());

        final Dag.Step s2 = new Dag.Step();
        s2.setId("s2");
        s2.setSourceId(p2.getId());
        s2.setTargetId(p3.getId());

        final Dag.Step s3 = new Dag.Step();
        s3.setId("s3");
        s3.setSourceId(p3.getId());
        s3.setTargetId(p4.getId());

        final Dag source = new Dag();
        source.setComponents(asList(sourceComponent, transformComponent, sinkComponent));
        source.setPorts(asList(p1, p2, p3, p4));
        source.setSteps(asList(s1, s2, s3));
        return source;
    }
}
