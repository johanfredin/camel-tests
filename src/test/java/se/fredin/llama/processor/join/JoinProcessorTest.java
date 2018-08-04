package se.fredin.llama.processor.join;

import org.junit.Before;
import org.junit.Test;
import se.fredin.llama.processor.Fields;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static se.fredin.llama.TestFixture.*;
import static se.fredin.llama.utils.ProcessorUtils.field;
import static se.fredin.llama.utils.ProcessorUtils.fields;

public class JoinProcessorTest {

    private List<Map<String, String>> mainEntries;
    private List<Map<String, String>> joiningEntries;

    @Before
    public void innit() {
        this.mainEntries = getList(
                getTestObjects(kvp("Id", "1"), kvp("Name", "Jonas"), kvp("Age", "25")),
                getTestObjects(kvp("Id", "2"), kvp("Name", "Leslie"), kvp("Age", "30")),
                getTestObjects(kvp("Id", "3"), kvp("Name", "Nils"), kvp("Age", "12"))
        );
        this.joiningEntries = getList(
                getTestObjects(kvp("Id", "1"), kvp("Pet", "Dog"), kvp("Color", "Blue")),
                getTestObjects(kvp("Id", "2"), kvp("Pet", "Cat"), kvp("Color", "Green")),
                getTestObjects(kvp("Id", "3"), kvp("Pet", "Lizard"), kvp("Color", "Yellow"))
        );
    }

    @Test
    public void testInnerJoin() {
        JoinProcessor joinProcessor = new JoinProcessor();
        joinProcessor.setJoinType(JoinType.INNER);
        joinProcessor.setEntity1Fields(new Fields(fields(field("Id"), field("Name"))));
        joinProcessor.setEntity2Fields(new Fields(fields(field("Pet"), field("Color"))));
        joinProcessor.setJoinKeys(Collections.singletonList(new JoinKey("Id", "Id")));

        List<Map<String, String>> result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        // Test results
        assertEquals("Result size should be 3", 3, result.size());

        // Test keys
        for (Map<String, String> map : result) {
            assertTrue("Keys contains Id", map.keySet().contains("Id"));
            assertTrue("Keys contains Name", map.keySet().contains("Name"));
            assertTrue("Keys contains Pet", map.keySet().contains("Pet"));
            assertTrue("Keys contains Color", map.keySet().contains("Color"));

            // Make sure we didn't get these
            assertFalse("Keys does not contain Age", map.keySet().contains("Age"));

            final String ID = map.get("Id");
            switch (ID) {
                case "1":
                    assertEquals("Name=Jonas", "Jonas", map.get("Name"));
                    assertEquals("Pet=Dog", "Dog", map.get("Pet"));
                    assertEquals("Color=Blue", "Blue", map.get("Color"));
                    break;
                case "2":
                    assertEquals("Name=Leslie", "Leslie", map.get("Name"));
                    assertEquals("Pet=Cat", "Cat", map.get("Pet"));
                    assertEquals("Color=Green", "Green", map.get("Color"));
                    break;
                case "3":
                    assertEquals("Name=Nils", "Nils", map.get("Name"));
                    assertEquals("Pet=Lizard", "Lizard", map.get("Pet"));
                    assertEquals("Color=Yellow", "Yellow", map.get("Color"));
                    break;
            }
        }


    }


}