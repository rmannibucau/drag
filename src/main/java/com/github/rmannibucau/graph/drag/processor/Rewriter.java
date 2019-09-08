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
package com.github.rmannibucau.graph.drag.processor;

import java.util.Collection;
import java.util.function.Function;

import com.github.rmannibucau.graph.drag.model.Dag;

public class Rewriter {
    public Dag rewrite(final Dag dag,
                       final Collection<Function<Dag, Dag>> rules) {
        int maxIterators = 1000;
        Dag out = dag;
        Dag previous;
        do {
            maxIterators--;
            previous = out;
            for (final Function<Dag, Dag> fn : rules) {
                out = fn.apply(dag);
            }
        } while (out != previous && maxIterators > 0);
        if (maxIterators == 0) {
            throw new IllegalStateException("rules seem to be looping, aborting");
        }
        return out;
    }
}
