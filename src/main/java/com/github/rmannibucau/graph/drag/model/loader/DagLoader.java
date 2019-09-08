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
package com.github.rmannibucau.graph.drag.model.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import javax.json.bind.JsonbBuilder;

import com.github.rmannibucau.graph.drag.model.Dag;

import lombok.RequiredArgsConstructor;

public interface DagLoader extends Supplier<Dag> {
    static DagLoader from(final String json) {
        return new JsonbImpl(json);
    }

    static DagLoader from(final Supplier<InputStream> json) {
        return new JsonbImpl(json);
    }

    static DagLoader from(final Dag dag) {
        return new Provided(dag);
    }

    @RequiredArgsConstructor
    class Provided implements DagLoader {
        private final Dag dag;

        @Override
        public Dag get() {
            return dag;
        }
    }

    @RequiredArgsConstructor
    class JsonbImpl implements DagLoader {
        private final Supplier<InputStream> model;

        private JsonbImpl(final String raw) {
            model = () -> new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public Dag get() {
            try (final javax.json.bind.Jsonb jsonb = JsonbBuilder.create();
                 final InputStream stream = model.get()) {
                return jsonb.fromJson(stream, Dag.class);
            } catch (final Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
