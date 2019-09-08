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

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.json.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dag {
    private List<Port> ports;
    private List<Step> steps;
    private List<Component> components;

    public Dag copy() {
        return new Dag(
            ports.stream().map(Port::copy).collect(toList()),
            steps.stream().map(Step::copy).collect(toList()),
            components.stream().map(Component::copy).collect(toList()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Port {
        private String id;
        private String nodeId;
        private JsonObject data;

        public Port copy() {
            return new Port(id, nodeId, data);
        }

        @Data
        public static class PortData {
            private String flowType;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {
        private String id;
        private String sourceId;
        private String targetId;
        private JsonObject data;

        public Step copy() {
            return new Step(id, sourceId, targetId, data);
        }
    }
}
