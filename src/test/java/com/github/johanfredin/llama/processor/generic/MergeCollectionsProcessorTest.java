package com.github.johanfredin.llama.processor.generic;

import com.github.johanfredin.llama.collection.LlamaList;
import com.github.johanfredin.llama.collection.LlamaMap;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MergeCollectionsProcessorTest {

    private List<Map<String, String>> mainContent;
    private List<Map<String, String>> mergeContent;

    @Before
    public void innit() {
        this.mainContent = LlamaList.of(
                LlamaMap.of("Id", "1", "Name", "Jan", "Age", "25"),
                LlamaMap.of("Id", "2", "Name", "Johan", "Age", "32")
        );

        this.mergeContent = LlamaList.of(
                LlamaMap.of("Id", "2", "Name", "Evelyn", "Age", "33", "Profession", "Web Design"),
                LlamaMap.of("Id", "3", "Name", "Leslie", "Age", "15", "Profession", "Programmer"),
                LlamaMap.of("Id", "4", "Name", "Bessie", "Age", "55", "Profession", "Happy")
        );
    }

    @Test
    public void merge() {
        var result = new MergeCollectionsProcessor().merge(this.mainContent, this.mergeContent);

        assertEquals("Size should be 5", 5, result.size());
        result.forEach(m -> assertEquals("4 headers", 4, m.keySet().size()));

        verifyContent(result, 0, "1", "Jan", "25", "");
        verifyContent(result, 1, "2", "Johan", "32", "");
        verifyContent(result, 2, "2", "Evelyn", "33", "Web Design");
        verifyContent(result, 3, "3", "Leslie", "15", "Programmer");
        verifyContent(result, 4, "4", "Bessie", "55", "Happy");

    }

    private void verifyContent(List<Map<String, String>> content, int index, String expectedId, String expectedName, String expectedAge, String expectedProfession) {
        assertEquals("Id=", expectedId, content.get(index).get("Id"));
        assertEquals("Name=", expectedName, content.get(index).get("Name"));
        assertEquals("Age=", expectedAge, content.get(index).get("Age"));
        assertEquals("Profession=", expectedProfession, content.get(index).get("Profession"));
    }

}