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
package com.github.johanfredin.llama.utils;

import org.junit.Test;
import com.github.johanfredin.llama.TestFixture;
import com.github.johanfredin.llama.pojo.Fields;
import com.github.johanfredin.llama.pojo.Keys;
import com.github.johanfredin.llama.collection.LlamaMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Test for the {@link JoinUtils} class
 */
public class JoinUtilsTest {

    @Test
    public void testGroupCollection() {
        assertNotNull("Param in is a list", TestFixture.mainEntries);

        var groupedCollection = JoinUtils.groupCollection(Keys.of("Id"), TestFixture.mainEntries);
        assertNotNull("Collection is not a map", groupedCollection);
        assertEquals("Map size=4", 4, groupedCollection.size());
        assertEquals("2 records where Id=1", 2, groupedCollection.get("1").size());
        assertEquals("1 records where Id=2", 1, groupedCollection.get("2").size());
        assertEquals("1 records where Id=3", 1, groupedCollection.get("3").size());
        assertEquals("1 records where Id=5", 1, groupedCollection.get("5").size());
    }

    @Test
    public void testKeysAsString() {
        var keysAsString = JoinUtils.keysAsString(TestFixture.mainEntries.get(0), Keys.of("Id", "Name", "Age"), JoinUtils.EXCHANGE_MAIN);
        assertEquals("Combined key length=", "1Jonas25".length(), keysAsString.length());
        assertTrue("Key contains 1", keysAsString.contains("1"));
        assertTrue("Key contains Jonas", keysAsString.contains("Jonas"));
        assertTrue("Key contains 25", keysAsString.contains("25"));
    }

    @Test
    public void testKeysAsStringKeyInJoining() {
        var keysAsString = JoinUtils.keysAsString(TestFixture.joiningEntries.get(0),
                Keys.of(LlamaMap.of(
                        "DummyKeyInMain", "Id",
                        "DummyPetInMain", "Pet",
                        "DummyColorInMain", "Color"
                )), JoinUtils.EXCHANGE_JOINING);

        assertEquals("Combined key=", "1DogBlue", keysAsString);
    }

    @Test
    public void testGroupingEnsureSameKeys() {
        Keys keys = Keys.of("Id", "Name", "Age");

        var bigList = new ArrayList<Map<String, String>>();
        IntStream.range(0, 20000).forEachOrdered(i -> {
            if (i < 15000) {
                var mapInList = Map.of("Id", "1", "Name", "Lars", "Age", "20");
                assertEquals("Combined key=1Lars20", "1Lars20",
                        JoinUtils.keysAsString(mapInList, keys, JoinUtils.EXCHANGE_MAIN));
                bigList.add(mapInList);
            } else {
                var mapInList = Map.of("Id", "2", "Name", "Lena", "Age", "30");
                assertEquals("Combined key=2Lena30", "2Lena30",
                        JoinUtils.keysAsString(mapInList, keys, JoinUtils.EXCHANGE_MAIN));
                bigList.add(mapInList);
            }
        });

        var groupedCollection = JoinUtils.groupCollection(keys, bigList);
        assertEquals("There should be 2 collections in map", 2, groupedCollection.size());
        assertEquals("15000 entries with key=1Lars20", 15000, groupedCollection.get("1Lars20").size());
        assertEquals("5000 entries with key=2Lena30", 5000, groupedCollection.get("2Lena30").size());
    }

    @Test
    public void testFetchHeader() {
        var header = JoinUtils.fetchHeader(JoinUtils.groupCollection(Keys.of("Id"), TestFixture.mainEntries));
        assertEquals("Headers fetched=3", 3, header.size());
        assertTrue("Header contains Id", header.contains("Id"));
        assertTrue("Header contains Name", header.contains("Name"));
        assertTrue("Header contains Age", header.contains("Age"));
    }

    @Test
    public void testGetFields() {
        var fields = JoinUtils.getFields(
                Map.of("Id", "1", "Name", "Lars", "Age", "25"),
                Fields.of(LlamaMap.of("Name", "Nombre", "Age", "Edad"))
        );

        assertEquals("Value of out field Nombre=Lars", "Lars", fields.get("Nombre"));
        assertEquals("Value of out field Edad=25", "25", fields.get("Edad"));

        assertNull("Original field name=Name no longer in map", fields.get("Name"));
        assertNull("Original field name=Age no longer in map", fields.get("Age"));

        assertEquals("Out map contains 2 entries", 2, fields.size());
    }

    @Test
    public void testCreateDummyMap() {
        var dummyMap = JoinUtils.createDummyMap(Set.of("Id", "Name", "Age"), Fields.of(LlamaMap.of("Id", "Identifier", "Name", "Namn")));
        assertEquals("Value of out field Identifier=\"\"", "", dummyMap.get("Identifier"));
        assertEquals("Value of out field Namn=\"\"", "", dummyMap.get("Namn"));

        assertNull("Original field name=Id no longer in map", dummyMap.get("Id"));
        assertNull("Original field name=Name no longer in map", dummyMap.get("Name"));

        assertEquals("Out map contains 2 entries", 2, dummyMap.size());
    }

    @Test
    public void testCreateMergedMap() {
        var mergedMap = JoinUtils.createMergedMap(TestFixture.mainEntries.get(0), TestFixture.joiningEntries.get(0));
        assertEquals("Merged amount of entries=5", 5, mergedMap.entrySet().size());
    }
}