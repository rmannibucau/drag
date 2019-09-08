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
package com.github.rmannibucau.graph.drag.test;

import static lombok.AccessLevel.PRIVATE;

import com.github.rmannibucau.graph.drag.model.Dag;
import com.github.rmannibucau.graph.drag.model.loader.DagLoader;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class DefaultModel {
    public static final Dag MODEL = DagLoader.from(() ->
            Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.dag.json")).get();
}
