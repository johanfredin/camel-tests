package se.fredin.llama.processor.join;

import org.junit.Before;
import org.junit.Test;
import se.fredin.llama.processor.Fields;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static se.fredin.llama.utils.ProcessorUtils.field;
import static se.fredin.llama.utils.ProcessorUtils.fields;


public class JoinCollectionsProcessorTest {

    private List<Map<String, String>> mainEntries;
    private List<Map<String, String>> joiningEntries;

    @Before
    public void innit() {
        this.mainEntries = List.of(
                Map.of("Id", "1", "Name", "Jonas", "Age", "25"),
                Map.of("Id", "2", "Name", "Leslie", "Age", "30"),
                Map.of("Id", "3", "Name", "Nils", "Age", "12")
        );
        this.joiningEntries = List.of(
                Map.of("Id", "1", "Pet", "Dog", "Color", "Blue"),
                Map.of("Id", "2", "Pet", "Cat", "Color", "Green"),
                Map.of("Id", "3", "Pet", "Lizard", "Color", "Yellow")
        );
    }

    @Test
    public void testInnerJoin() {
        var joinProcessor = new JoinCollectionsProcessor();
        joinProcessor.setJoinType(JoinType.INNER);
        joinProcessor.setEntity1Fields(new Fields(fields(field("Id"), field("Name"))));
        joinProcessor.setEntity2Fields(new Fields(fields(field("Pet"), field("Color"))));
        joinProcessor.setJoinKeys(Collections.singletonList(new JoinKey("Id", "Id")));

        var result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        // Test results
        assertEquals("Result size should be 3", 3, result.size());

        // Test keys
        for (var map : result) {
            assertTrue("Keys contains Id", map.keySet().contains("Id"));
            assertTrue("Keys contains Name", map.keySet().contains("Name"));
            assertTrue("Keys contains Pet", map.keySet().contains("Pet"));
            assertTrue("Keys contains Color", map.keySet().contains("Color"));

            // Make sure we didn't get these
            assertFalse("Keys does not contain Age", map.keySet().contains("Age"));

            final var ID = map.get("Id");
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