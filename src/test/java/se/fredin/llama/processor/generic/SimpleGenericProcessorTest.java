package se.fredin.llama.processor.generic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import se.fredin.llama.TestFixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
