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
package com.github.johanfredin.llama.processor.generic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.github.johanfredin.llama.TestFixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract superclass for {@link GenericProcessor} test cases
 */
@RunWith(JUnit4.class)
public abstract class SimpleGenericProcessorTest {

    @Test
    public abstract void testProcessData();

    /**
     * Create a mutable list of {@link TestFixture#mainEntries}. Some of the generic processors
     * require this functionality.
     * @return a mutable version of TestFixture.mainEntries
     */
    protected List<Map<String, String>> getEntries() {
        List<Map<String, String>> entries = new ArrayList<>();
        TestFixture.mainEntries
                .forEach(m -> entries.add(new HashMap<>(m)));
        return entries;
    }

}
