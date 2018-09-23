package se.fredin.llama.processor.join;

import org.junit.Before;
import org.junit.Test;
import se.fredin.llama.processor.Fields;
import se.fredin.llama.utils.LlamaUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static se.fredin.llama.utils.LlamaUtils.field;
import static se.fredin.llama.utils.LlamaUtils.fields;


public class JoinCollectionsProcessorTest {

    private List<Map<String, String>> mainEntries;
    private List<Map<String, String>> joiningEntries;

    @Before
    public void innit() {
        this.mainEntries = List.of(
                Map.of("Id", "1", "Name", "Jonas", "Age", "25"),
                Map.of("Id", "1", "Name", "Lena", "Age", "26" ),
                Map.of("Id", "2", "Name", "Leslie", "Age", "30"),
                Map.of("Id", "3", "Name", "Nils", "Age", "12")
        );
        this.joiningEntries = List.of(
                Map.of("Id", "1", "Pet", "Dog", "Color", "Blue"),
                Map.of("Id", "2", "Pet", "Cat", "Color", "Green"),
                Map.of("Id", "3", "Pet", "Lizard", "Color", "Yellow"),
                Map.of("Id", "4", "Pet", "Iguana", "Color", "Orange")
        );
    }

    @Test
    public void testInnerJoin() {
        var joinProcessor = new JoinCollectionsProcessor();
        joinProcessor.setJoinType(JoinType.INNER);
        joinProcessor.setEntity1Fields(new Fields(field("Id"), field("Name")));
        joinProcessor.setEntity2Fields(new Fields(field("Pet"), field("Color")));
        joinProcessor.setJoinKeys(Collections.singletonList(new JoinKey("Id", "Id")));

        var result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        // Make sure that there are 2 entries associated with id=1
        var mapOfLists = JoinUtils.groupCollection(new JoinKey("Id"), JoinUtils.EXCHANGE_MAIN, result);
        assertEquals("There should be 2 records associated with Id=1", 2, mapOfLists.get("1").size());

        // Test results
        assertEquals("Result size should be 4", 4, result.size());

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
                case "1": // There are 2 records with Id=1
                    assertTrue("Name=Jonas||Lena", map.get("Name").equals("Jonas") || map.get("Name").equals("Lena"));
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