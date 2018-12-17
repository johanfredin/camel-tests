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

import org.apache.camel.Exchange;
import org.junit.Test;
import com.github.johanfredin.llama.TestFixture;
import com.github.johanfredin.llama.bean.LlamaBean;
import com.github.johanfredin.llama.mock.exchange.MockExchange;
import com.github.johanfredin.llama.mock.exchange.MockMessage;
import com.github.johanfredin.llama.collection.LlamaMap;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Test for the {@link LlamaUtils} class
 */
public class LlamaUtilsTest {

    @Test
    public void testIsTrueAny() {
        assertTrue("Any is true", LlamaUtils.isTrueAny(true, false, false));
        assertFalse("None is not true", LlamaUtils.isTrueAny(false, false, false));
    }

    @Test
    public void testIsTrueAll() {
        assertTrue("All is true", LlamaUtils.isTrueAll(true, true, true));
        assertFalse("Is not true all", LlamaUtils.isTrueAll(true, true, false));
    }

    @Test
    public void testIsTrue() {
        assertTrue("All is true", LlamaUtils.isTrue("&&", true, true, true));
        assertFalse("Is not true all", LlamaUtils.isTrue("&&", true, true, false));

        assertTrue("Any is true", LlamaUtils.isTrue("||", true, false, false));
        assertFalse("None is not true", LlamaUtils.isTrue("||", false, false, false));
    }

    @Test
    public void testGetTransformedUrl() {
        var transformedUrl = LlamaUtils.getTransformedUrl(
                "d:/files/some/directory/myImage.jpg",
                "D:\\files\\some\\DiRectory\\",
                "http://www.outurl.com/"
        );
        assertEquals("Out url=http://www.outurl.com/myImage.jpg", "http://www.outurl.com/myimage.jpg", transformedUrl);
    }

    @Test
    public void testGetMergedMap() {
        var main = Map.of("Name", "Jonas", "Age", "50");
        var joining = Map.of("Name", "Lena", "Age", "25", "Profession", "Programmer");

        // Verify we override duplicates in this step
        var result = LlamaUtils.getMergedMap(main, joining);
        assertEquals("Merged amount of entries=3", 3, result.entrySet().size());
        assertEquals("Name should be Lena", "Lena", result.get("Name"));
        assertEquals("Age=25", "25", result.get("Age"));
        assertNotNull("Profession should exist", result.get("Profession"));

        // Verify we don't override duplicates in this step
        result = LlamaUtils.getMergedMap(List.of(main, joining), false);
        assertEquals("Merged amount of entries=3", 3, result.entrySet().size());
        assertEquals("Name=Jonas", "Jonas", result.get("Name"));
        assertEquals("Age=50", "50", result.get("Age"));
        assertNotNull("Profession should exist", result.get("Profession"));
    }


    @Test
    public void testWithinRange() {
        Object something = "250";
        assertTrue("object within range", LlamaUtils.withinRange(something, 10, 300));
        assertFalse("object not withing range", LlamaUtils.withinRange(something, 100, 200));
    }

    @Test
    public void testIsHeader() {
        var header = LlamaMap.of("Name", "Name", "Age", "Age");
        assertTrue("Map qualifies as header", LlamaUtils.isHeader(header));

        header.put("Name", "Klas");
        assertFalse("Map no longer qualifies as header", LlamaUtils.isHeader(header));
    }

    @Test
    public void testAsLlamaBeanList() {
        var mockItems = TestFixture.getMockItems();
        var beans = LlamaUtils.asLlamaBeanList(getExchange(mockItems));

        assertTrue("2 entries in original and fetched", mockItems.size() == beans.size());
        assertTrue("Same entries", beans.containsAll(mockItems));
    }

    @Test
    public void testAsListOfMaps() {
        var entries = TestFixture.mainEntries;
        var result = LlamaUtils.asListOfMaps(getExchange(entries));

        assertTrue("Beans is a list", result instanceof ArrayList);
        assertFalse("Beans is not a linked list", result instanceof LinkedList);

        assertTrue("5 entries in original and fetched", entries.size() == result.size());
        assertTrue("Same entries", entries.containsAll(result));

        result.forEach(m -> {
            assertTrue("Each entry in list should be a map", m instanceof Map);
        });
    }

    @Test
    public void testAsLinkedListOfMaps() {
        var entries = TestFixture.mainEntries;
        var result = LlamaUtils.asLinkedListOfMaps(getExchange(entries));

        assertFalse("Beans is a list", result instanceof ArrayList);
        assertTrue("Beans is not a linked list", result instanceof LinkedList);

        assertTrue("5 entries in original and fetched", entries.size() == result.size());
        assertTrue("Same entries", entries.containsAll(result));

        result.forEach(m -> {
            assertTrue("Each entry in list should be a map", m instanceof Map);
        });
    }

    @Test
    public void testAsTypedList() {
        var entries = List.of(1, 2, 3, 4);
        var result = LlamaUtils.<Integer>asTypedList(getExchange(entries));

        assertTrue("Beans is a list", result instanceof ArrayList);

        assertTrue("4 entries in original and fetched", entries.size() == result.size());
        assertTrue("Same entries", entries.containsAll(result));

        result.forEach(m -> {
            assertTrue("Each entry in list should be a map", m instanceof Integer);
        });
    }

    @Test
    public void testAsTypedMap() {
        var map = Map.of(1, "Jan", 2, "Erick");
        var result = LlamaUtils.<Integer, String>asTypedMap(getExchange(map));

        assertTrue("Same amount", map.size() == result.size());
        assertTrue("Same data", map.values().containsAll(result.values()));
    }

    @Test
    public void asLlamaBeanMap() {
        var beans = TestFixture.getMockItems();
        var result = LlamaUtils.asLlamaBeanMap(getExchange(beans));

        assertEquals("2 entries in map", 2, result.size());
        assertEquals("1 entry with id 1", 1, result.get("Abc123").size());
        assertEquals("1 entry with id 2", 1, result.get("Abc1234").size());

        result.entrySet()
                .forEach(me -> {
                    assertTrue("Keys are Serializable", me.getKey() instanceof Serializable);
                    assertTrue("Values are lists", me.getValue() instanceof List);
                    me.getValue()
                            .forEach(b -> assertTrue("Value is a llamabean", b instanceof LlamaBean));
                });
    }

    @Test
    public void testReconnectHeaderMapParam() {
        verifyHeader(LlamaUtils.reconnectHeader(TestFixture.mainEntries));
    }

    @Test
    public void testReconnectHeaderExchangeParam() {
        var exchange = LlamaUtils.reconnectHeader(getExchange(TestFixture.mainEntries));
        verifyHeader(LlamaUtils.asLinkedListOfMaps(exchange).get(0));
    }

    void verifyHeader(Map<String, String> header) {
        assertEquals("Header size=3", 3, header.size());
        assertEquals("Header name/value contains Id", "Id", header.get("Id"));
        assertEquals("Header name/value contains Name", "Name", header.get("Name"));
        assertEquals("Header name/value contains Age", "Age", header.get("Age"));
    }

    private Exchange getExchange(Object body) {
        var in = new MockMessage();
        in.setBody(body);
        var exchange = new MockExchange();
        exchange.setIn(in);

        return exchange;
    }


}