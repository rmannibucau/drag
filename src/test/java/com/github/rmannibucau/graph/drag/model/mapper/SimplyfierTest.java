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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.github.rmannibucau.graph.drag.model.SimplifiedDag;
import com.github.rmannibucau.graph.drag.test.DefaultModel;

class SimplyfierTest {
    @Test
    void simplify() {
        final SimplifiedDag simplified = new Simplyfier().simplify(DefaultModel.MODEL);
        assertEquals(12, simplified.getNodes().size());
        assertEquals(11, simplified.getEdges().size());
        assertEquals(emptyList(), simplified.getEdges().stream().filter(it -> !isValid(it)).collect(toList()));
    }

    private boolean isValid(final SimplifiedDag.Edge it) {
        return it.getStep() != null && it.getFrom() != null && it.getTo() != null;
    }
}
