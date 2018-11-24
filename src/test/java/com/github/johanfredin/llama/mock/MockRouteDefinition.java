/*
 * Copyright 2018 Johan Fredin
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
package com.github.johanfredin.llama.mock;

import java.util.List;

public class MockRouteDefinition {

    private String routeId;
    private List<EndpointToMockEndpoint> enpointsTomock;

    public MockRouteDefinition(String routeId, List<EndpointToMockEndpoint> enpointsTomock) {
        this.routeId = routeId;
        this.enpointsTomock = enpointsTomock;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public List<EndpointToMockEndpoint> getEnpointsTomock() {
        return enpointsTomock;
    }

    public void setEnpointsTomock(List<EndpointToMockEndpoint> enpointsTomock) {
        this.enpointsTomock = enpointsTomock;
    }


    @Override
    public String toString() {
        return "MockRouteDefinition{" +
                "routeId='" + routeId + '\'' +
                ", enpointsTomock=" + enpointsTomock +
                '}';
    }
}
