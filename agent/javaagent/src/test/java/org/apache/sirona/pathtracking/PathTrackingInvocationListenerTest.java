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
package org.apache.sirona.pathtracking;

import org.apache.sirona.configuration.ioc.IoCs;
import org.apache.sirona.javaagent.AgentArgs;
import org.apache.sirona.javaagent.JavaAgentRunner;
import org.apache.sirona.store.DataStoreFactory;
import org.apache.sirona.store.tracking.InMemoryPathTrackingDataStore;
import org.apache.sirona.store.tracking.PathTrackingDataStore;
import org.apache.sirona.tracking.PathTrackingEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

/**
 * @author Olivier Lamy
 */
@RunWith( JavaAgentRunner.class )
public class PathTrackingInvocationListenerTest
{

    @Test
    @AgentArgs( value ="",
    sysProps = "project.build.directory=${project.build.directory}|sirona.agent.debug=${sirona.agent.debug}|org.apache.sirona.configuration.sirona.properties=${project.build.directory}/test-classes/pathtracking/sirona.properties")
    public void simpleTest()
    throws Exception
    {

        App app = new App();
        app.beer();

        DataStoreFactory dataStoreFactory = IoCs.findOrCreateInstance( DataStoreFactory.class );

        InMemoryPathTrackingDataStore ptds =
            InMemoryPathTrackingDataStore.class.cast( dataStoreFactory.getPathTrackingDataStore() );

        Map<String, List<PathTrackingEntry>> all = ptds.retrieveAll();

        Assert.assertTrue( !all.isEmpty() );

        PathTrackingEntry first = all.entrySet().iterator().next().getValue().get( 0 );

        System.out.println("first entry: " + first);

    }


    public static class App
    {
        public void foo() throws Exception
        {
            Thread.sleep( 1000 );
        }

        public void beer() throws Exception
        {
            this.foo();
        }
    }

}
