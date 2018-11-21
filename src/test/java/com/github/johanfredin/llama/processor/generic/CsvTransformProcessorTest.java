/**
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

import com.github.johanfredin.llama.TestFixture;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test for the {@link CsvTransformProcessor}
 */
public class CsvTransformProcessorTest extends SimpleGenericProcessorTest{

    @Override
    public void testProcessData() {
        var records = new ArrayList<>(getEntries());

        assertEquals("Un filtered records=5", 5, records.size());
        records.forEach(
                m -> assertNotEquals("Age is not 100", "100", m.get("Age"))
        );


        var processor = new CsvTransformProcessor(m -> m.put("Age", "100"), false);
        var result = processor.processData(records);

        assertEquals("Still the same amount of entries", TestFixture.mainEntries.size(), result.size());

        result.forEach(
                m -> assertEquals("Age is now 100", "100", m.get("Age"))
        );
    }


}