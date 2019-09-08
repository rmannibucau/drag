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

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyOrderStrategy;

import org.junit.jupiter.api.Test;
import com.github.rmannibucau.graph.drag.test.DefaultModel;

class DagJsonbSerializationTest {
    @Test
    void toJson() {
        try (final javax.json.bind.Jsonb jsonb = JsonbBuilder.create(
                new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL));
             final BufferedReader expected = new BufferedReader(new InputStreamReader(requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("output.dag.json"), "output.dag.json missing")))) {
            assertEquals(expected.lines().collect(joining("\n")).trim(), jsonb.toJson(DefaultModel.MODEL));
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
