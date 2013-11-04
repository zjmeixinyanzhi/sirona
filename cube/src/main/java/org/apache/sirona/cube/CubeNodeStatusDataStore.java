/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sirona.cube;

import org.apache.sirona.configuration.Configuration;
import org.apache.sirona.status.NodeStatus;
import org.apache.sirona.status.ValidationResult;
import org.apache.sirona.store.status.PeriodicNodeStatusDataStore;

public class CubeNodeStatusDataStore extends PeriodicNodeStatusDataStore {
    private static final String VALIDATION_TYPE = "validation";

    private final Cube cube = Configuration.findOrCreateInstance(CubeBuilder.class).build();

    @Override
    protected void reportStatus(final NodeStatus nodeStatus) {
        final long ts = System.currentTimeMillis();
        final StringBuilder events = cube.newEventStream();
        for (final ValidationResult result : nodeStatus.getResults()) {
            cube.buildEvent(events, VALIDATION_TYPE, ts, new MapBuilder()
                .add("message", result.getMessage())
                .add("status", result.getStatus().name())
                .add("name", result.getName())
                .map());
        }
        cube.post(events);
    }
}
