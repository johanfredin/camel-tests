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

public class EndpointToMockEndpoint {

    private String realEndpoint;
    private String mockedEnpoint;

    public EndpointToMockEndpoint(String realEndpoint, String mockedEnpoint) {
        this.realEndpoint = realEndpoint;
        this.mockedEnpoint = mockedEnpoint;
    }

    public String getRealEndpoint() {
        return realEndpoint;
    }

    public void setRealEndpoint(String realEndpoint) {
        this.realEndpoint = realEndpoint;
    }

    public String getMockedEnpoint() {
        return mockedEnpoint;
    }

    public void setMockedEnpoint(String mockedEnpoint) {
        this.mockedEnpoint = mockedEnpoint;
    }

    @Override
    public String toString() {
        return "EndpointToMock{" +
                "realEndpoint='" + realEndpoint + '\'' +
                ", mockedEnpoint='" + mockedEnpoint + '\'' +
                '}';
    }
}
