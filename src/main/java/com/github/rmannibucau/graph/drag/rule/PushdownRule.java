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

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonCollectors;

import com.github.rmannibucau.graph.drag.lang.Tuple2;
import com.github.rmannibucau.graph.drag.model.Component;
import com.github.rmannibucau.graph.drag.model.Dag;
import com.github.rmannibucau.graph.drag.model.SimplifiedDag;
import com.github.rmannibucau.graph.drag.model.mapper.Simplyfier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PushdownRule implements Function<Dag, Dag> {
    private static final String INJECTED_CONFIG_NAME = "$pushdown";

    private final Configuration rules;
    private final JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(emptyMap());

    public PushdownRule() {
        this(new Configuration( // todo
            "noop-todo",
            emptyMap()));
    }

    @Override
    public Dag apply(final Dag dag) {
        if (rules.getNoopComponentId() == null) {
            return dag;
        }

        final Collection<Component> pushdownComponents = findPushdownComponents(dag);
        if (pushdownComponents.isEmpty()) { // quick exit path
            return dag;
        }

        final Simplyfier simplyfier = new Simplyfier();
        final SimplifiedDag graph = simplyfier.simplify(dag);

        // for each pushdown component we identify a component we can push to
        // it implies to modify the dag to:
        // 1. replace pushed down components with a noop component
        // 2. enrich the component it is pushed to with $pushdown configuration
        final Dag copy = dag.copy();
        pushdownComponents.stream()
            .map(it -> {
                final PushdownComponent pushdownComponent = rules.getPushdownCompatibleComponents().get(it.getType());
                return new Tuple2<>(it, graph.findUpStream(it.getId(), pushdownComponent::isFiend).collect(toList()));
            })
            .filter(pair -> !pair.getSecond().isEmpty())
            .peek(this::log)
            .forEach(pushdownPair -> doPushdown(copy, pushdownPair));

        if (copy.equals(dag)) { // trivial loop protection (see com.github.rmannibucau.graph.drag.processor.Rewriter)
            return dag;
        }

        // TODO: add a SPI here?
        // here we can add logic to rewrite the configuration (ex: take $pushdown and rewrite the sql query)

        return copy;
    }

    private void doPushdown(final Dag copy, final Tuple2<Component, List<Component>> pushdownPair) {
        final Map<String, Component> pushedDownComponentIndex = copy.getComponents().stream()
                .filter(c -> Objects.equals(c.getId(), pushdownPair.getFirst().getId()) ||
                        pushdownPair.getSecond().stream().anyMatch(pc -> Objects.equals(c.getId(), pc.getId())))
                .collect(toMap(Component::getId, c -> c));

        final Component targetComponent = pushedDownComponentIndex.remove(pushdownPair.getFirst().getId());
        if (targetComponent == null) {
            log.warn("Can't pushdown to '{}' (type='{}'), component not found", targetComponent.getId(), targetComponent.getType());
            return;
        }

        // pushdown data
        pushedDownComponentIndex.values().forEach(receiverComponent -> {
            final JsonObjectBuilder pushedDownMeta = jsonBuilderFactory.createObjectBuilder()
                    .add("$from", targetComponent.getType())
                    .add("$configuration", ofNullable(targetComponent.getData()).orElseGet(() -> jsonBuilderFactory.createObjectBuilder().build()));
            receiverComponent.setData(
                    ofNullable(receiverComponent.getData())
                        .map(data -> {
                            final JsonArray pushdown = (data.containsKey(INJECTED_CONFIG_NAME) ?
                                        jsonBuilderFactory.createArrayBuilder(data.getJsonArray(INJECTED_CONFIG_NAME)) :
                                        jsonBuilderFactory.createArrayBuilder())
                                    .add(pushedDownMeta).build();
                            return jsonBuilderFactory.createObjectBuilder(data.entrySet().stream()
                                .filter(it -> !INJECTED_CONFIG_NAME.equals(it.getKey()))
                                .collect(JsonCollectors.toJsonObject()))
                                .add(INJECTED_CONFIG_NAME, pushdown)
                                .build();
                        })
                        .orElseGet(() -> jsonBuilderFactory.createObjectBuilder()
                            .add(INJECTED_CONFIG_NAME, jsonBuilderFactory.createArrayBuilder().add(pushedDownMeta).build())
                            .build()));
        });

        // move target component to a plain noop (we could drop it at runtime later)
        targetComponent.setType(rules.getNoopComponentId());
        targetComponent.setData(jsonBuilderFactory.createObjectBuilder().build());
    }

    private Collection<Component> findPushdownComponents(final Dag dag) {
        return dag.getComponents().stream().filter(this::isPushdownComponent).collect(toList());
    }

    private boolean isPushdownComponent(final Component component) {
        return rules.getPushdownCompatibleComponents() != null &&
                rules.getPushdownCompatibleComponents().entrySet().stream()
                    .anyMatch(it -> Objects.equals(component.getType(), it.getKey()));
    }

    private void log(final Tuple2<Component, List<Component>> pair) {
        if (!log.isInfoEnabled()) {
            return;
        }
        log.info("Trying to pushdown '{}'",
                pair.getFirst().getId() +
                        ", type=" + pair.getFirst().getType() +
                        ", to " +
                        pair.getSecond().stream()
                                .map(c -> c.getId() + ", type=" + c.getType())
                                .collect(joining(", ", "[", "]")));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Configuration {
        private String noopComponentId;

        // key == id of the pushdown component
        private Map<String, PushdownComponent> pushdownCompatibleComponents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PushdownComponent {
        private Collection<String> friends;

        private boolean isFiend(final Component component) {
            return friends != null && friends.contains(component.getType());
        }
    }
}
