package se.fredin.llama.processor.generic;

import se.fredin.llama.TestFixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CsvTransformProcessorTest extends SimpleGenericProcessorTest{

    @Override
    public void testProcessData() {
        var records = TestFixture.mainEntries;

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