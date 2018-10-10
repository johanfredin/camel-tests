package se.fredin.llama.processor.generic;

import se.fredin.llama.TestFixture;
import se.fredin.llama.utils.LlamaUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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