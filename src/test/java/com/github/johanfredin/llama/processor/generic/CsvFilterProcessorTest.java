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

import com.github.johanfredin.llama.utils.LlamaUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test for the {@link SimpleGenericProcessor}
 */
public class CsvFilterProcessorTest extends SimpleGenericProcessorTest {

    @Override
    public void testProcessData() {
        var records = getEntries();

        assertEquals("Un filtered records=5", 5, records.size());

        var processor = new CsvFilterProcessor(m -> LlamaUtils.withinRange(m.get("Age"), 20, 30), false);
        var result = processor.processData(records);

        assertEquals("When filtering out ages bigger than 20 and smaller than 30 we should have 2 records left", 2, result.size());
        assertFalse("There should be no header at first index", LlamaUtils.isHeader(result.get(0)));
    }


}