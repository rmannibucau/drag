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

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.github.rmannibucau.graph.drag.model.Component;
import com.github.rmannibucau.graph.drag.model.Dag;
import com.github.rmannibucau.graph.drag.test.DefaultModel;

class DagLoaderTest {
    @Test
    void loadWithJsonb() {
        final Dag dag = DefaultModel.MODEL;
        assertEquals(12, dag.getComponents().size());
        assertEquals(22, dag.getPorts().size());
        assertEquals(11, dag.getSteps().size());
        assertEquals("cc50d69d-d33e-45de-b06e-ade19c75bcba," +
                "e4da2903-53b6-431f-9e4c-5f828d2a273d," +
                "5c75782867a91c0ad43c82ea," +
                "c02ac818-4620-4c39-ae06-c87396bad484," +
                "9cd44437-d5e9-47be-ab48-5b1d70af367f," +
                "b30da104-156a-4cce-8015-28f4d707da45," +
                "73e9baa8-b163-4d04-9dd4-81741ef5f338," +
                "e003b3d7-16e3-4ce1-a656-d47df2ca0e65," +
                "5c7578c667a91c0ad43c83d5,5c76a4df1328d850e0add6e0," +
                "5c76a74188c7a959dcf84f8e," +
                "cd32c6c6-291a-4547-ae13-f46f55400550",
                dag.getComponents().stream().map(Component::getId).collect(joining(",")));
        assertEquals("5c76a4df1328d850e0add6da," +
                "5c75790967a91c0ad43c8415," +
                "5c76a4911328d850e0add681," +
                "5c76a4df1328d850e0add6d9," +
                "5c75828a24f1ccbaaf5c81ec," +
                "5c76bb52eb1c8e133ae95b68," +
                "5c75797867a91c0ad43c8566," +
                "5c76a4911328d850e0add682," +
                "5c76a78d88c7a959dcf84fec," +
                "5c76a4cb1328d850e0add6b2," +
                "5c7578fa67a91c0ad43c83f3," +
                "5c75828a24f1ccbaaf5c81ed," +
                "5c76a4cb1328d850e0add6b3," +
                "5c75790967a91c0ad43c8416," +
                "5c76a74188c7a959dcf84f87," +
                "5c76a74188c7a959dcf84f8b," +
                "5c7578fa67a91c0ad43c83f4," +
                "5c76a4ee1328d850e0add711," +
                "5c76a74188c7a959dcf84f88," +
                "5c76a6071328d850e0add790," +
                "5c76a6071328d850e0add78f," +
                "5c76a4df1328d850e0add6dd", dag.getPorts().stream().map(Dag.Port::getId).collect(joining(",")));
        assertEquals("5c7578fa67a91c0ad43c83f8," +
                "5c76a4df1328d850e0add6e2," +
                "5c76a4cb1328d850e0add6b7," +
                "5c76a74188c7a959dcf84f8d," +
                "5c76a74188c7a959dcf84f90," +
                "5c76a4df1328d850e0add6df," +
                "5c76a4df1328d850e0add6de," +
                "5c76a6071328d850e0add794," +
                "5c75790967a91c0ad43c841a," +
                "5c76a74188c7a959dcf84f8c," +
                "5c75828a24f1ccbaaf5c81f1", dag.getSteps().stream().map(Dag.Step::getId).collect(joining(",")));
    }
}
