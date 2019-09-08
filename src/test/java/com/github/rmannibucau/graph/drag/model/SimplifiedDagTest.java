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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import com.github.rmannibucau.graph.drag.model.mapper.Simplyfier;
import com.github.rmannibucau.graph.drag.test.DefaultModel;

class SimplifiedDagTest {
    @Test
    void findUpStream() { // from field selector we extract the source
        assertUpStream(
                "bG9jYWxpbyNMb2NhbElPI0ZpeGVkRmxvd0lucHV0UnVudGltZQ", "c02ac818-4620-4c39-ae06-c87396bad484",
                singletonList("5c75782867a91c0ad43c82ea"));
    }

    @Test
    void findNoUpStream() { // from an aggregate we don't manage to find the python processor
        assertUpStream(
                "cHJvY2Vzc2luZy1iZWFtI1Byb2Nlc3NpbmcjUHl0aG9u", "e003b3d7-16e3-4ce1-a656-d47df2ca0e65",
                emptyList());
    }

    @Test
    void graphviz() {
        assertEquals(
            "digraph D {\n" +
            "\n" +
            "  node_0 [label=\"id=cc50d69d-d33e-45de-b06e-ade19c75bcba\\ntype=cHJvY2Vzc2luZy1iZWFtI1Byb2Nlc3NpbmcjSm9pbg\"]\n" +
            "  node_1 [label=\"id=e4da2903-53b6-431f-9e4c-5f828d2a273d\\ntype=cHJvY2Vzc2luZy1iZWFtI1Byb2Nlc3NpbmcjQWdncmVnYXRl\"]\n" +
            "  node_2 [label=\"id=5c75782867a91c0ad43c82ea\\ntype=bG9jYWxpbyNMb2NhbElPI0ZpeGVkRmxvd0lucHV0UnVudGltZQ\"]\n" +
            "  node_3 [label=\"id=c02ac818-4620-4c39-ae06-c87396bad484\\ntype=cHJvY2Vzc2luZy1iZWFtI1Byb2Nlc3NpbmcjRmllbGRTZWxlY3Rvcg\"]\n" +
            "  node_4 [label=\"id=9cd44437-d5e9-47be-ab48-5b1d70af367f\\ntype=cHJvY2Vzc2luZy1iZWFtI1Byb2Nlc3NpbmcjRmllbGRTZWxlY3Rvcg\"]\n" +
            "  node_5 [label=\"id=b30da104-156a-4cce-8015-28f4d707da45\\ntype=cHJvY2Vzc2luZy1iZWFtI1Byb2Nlc3NpbmcjUmVwbGljYXRl\"]\n" +
            "  node_6 [label=\"id=73e9baa8-b163-4d04-9dd4-81741ef5f338\\ntype=cHJvY2Vzc2luZy1iZWFtI1Byb2Nlc3NpbmcjRmlsdGVy\"]\n" +
            "  node_7 [label=\"id=e003b3d7-16e3-4ce1-a656-d47df2ca0e65\\ntype=cHJvY2Vzc2luZy1iZWFtI1Byb2Nlc3NpbmcjQWdncmVnYXRl\"]\n" +
            "  node_8 [label=\"id=5c7578c667a91c0ad43c83d5\\ntype=bG9jYWxpbyNMb2NhbElPI0Rldk51bGxPdXRwdXRSdW50aW1l\"]\n" +
            "  node_9 [label=\"id=5c76a4df1328d850e0add6e0\\ntype=bG9jYWxpbyNMb2NhbElPI0Rldk51bGxPdXRwdXRSdW50aW1l\"]\n" +
            "  node_10 [label=\"id=5c76a74188c7a959dcf84f8e\\ntype=bG9jYWxpbyNMb2NhbElPI0Rldk51bGxPdXRwdXRSdW50aW1l\"]\n" +
            "  node_11 [label=\"id=cd32c6c6-291a-4547-ae13-f46f55400550\\ntype=cHJvY2Vzc2luZy1iZWFtI1Byb2Nlc3NpbmcjUHl0aG9u\"]\n" +
            "\n" +
            "  node_2 -> node_7\n" +
            "  node_5 -> node_9\n" +
            "  node_0 -> node_3\n" +
            "  node_1 -> node_6\n" +
            "  node_6 -> node_10\n" +
            "  node_11 -> node_5\n" +
            "  node_5 -> node_0\n" +
            "  node_3 -> node_1\n" +
            "  node_7 -> node_4\n" +
            "  node_6 -> node_8\n" +
            "  node_4 -> node_11\n" +
            "\n" +
            "}",
            new Simplyfier().simplify(DefaultModel.MODEL).toGraphviz());
    }

    private void assertUpStream(final String searchedType, final String rootNode, final List<String> expected) {
        final SimplifiedDag dag = new Simplyfier().simplify(DefaultModel.MODEL);
        final List<String> source = dag.findUpStream(rootNode, component -> searchedType.equals(component.getType()))
                .map(Component::getId)
                .collect(toList());
        assertEquals(expected, source);
    }
}
