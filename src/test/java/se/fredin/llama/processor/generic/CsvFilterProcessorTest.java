package se.fredin.llama.processor.generic;

import org.junit.Test;
import se.fredin.llama.TestFixture;
import se.fredin.llama.utils.LlamaUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CsvFilterProcessorTest {

    @Test
    public void testFilterRecordsNoHeader() {
        var result = assertCommonLogic(false);

        assertEquals("When filtering out ages bigger than 20 and smaller than 30 we should have 2 records left", 2, result.size());
        assertFalse("There should be no header at first index", LlamaUtils.isHeader(result.get(0)));
    }

    @Test
    public void testFilterRecordsHeader() {
        var result = assertCommonLogic(true);

        assertEquals("When filtering out ages bigger than 20 and smaller than 30 AND including header we should have 3 records left", 3, result.size());
        assertTrue("There should be no header at first index", LlamaUtils.isHeader(result.get(0)));
    }

    private List<Map<String, String>> assertCommonLogic(boolean includeHeader) {
        var records = TestFixture.mainEntries;

        assertEquals("Un filtered records=5", 5, records.size());

        var processor = new CsvFilterProcessor(m -> LlamaUtils.withinRange(m.get("Age"), 20, 30), includeHeader);
        return processor.filterRecords(records);
    }

}