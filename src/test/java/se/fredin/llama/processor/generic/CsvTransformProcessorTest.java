package se.fredin.llama.processor.generic;

import org.junit.Test;
import se.fredin.llama.TestFixture;
import se.fredin.llama.utils.LlamaUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CsvTransformProcessorTest extends SimpleGenericProcessorTest{

    @Override
    public void testProcessData() {
        var records = TestFixture.mainEntries;

        assertEquals("Un filtered records=5", 5, records.size());

        var processor = new CsvTransformProcessor(m -> m.put("Age", "100"), false);
        var result = processor.processData(records);

        assertEquals("Still the same amount of entries", TestFixture.mainEntries.size(), result.size());

        result.forEach(
                m -> assertEquals("Age is now 100", "100", m.get("Age"))
        );
    }


}