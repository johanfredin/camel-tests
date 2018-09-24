package se.fredin.llama.processor.join;

import org.junit.Before;
import org.junit.Test;
import se.fredin.llama.processor.Fields;
import se.fredin.llama.processor.ResultType;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static se.fredin.llama.utils.LlamaUtils.field;


public class JoinCollectionsProcessorTest {

    private List<Map<String, String>> mainEntries;
    private List<Map<String, String>> joiningEntries;

    @Before
    public void innit() {
        this.mainEntries = List.of(
                Map.of("Id", "1", "Name", "Jonas", "Age", "25"),
                Map.of("Id", "1", "Name", "Lena", "Age", "26"),
                Map.of("Id", "2", "Name", "Leslie", "Age", "30"),
                Map.of("Id", "3", "Name", "Nils", "Age", "12"),
                Map.of("Id", "5", "Name", "Eddie", "Age", "56")
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
        var joinProcessor = getProcessor(JoinType.INNER);

        var result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        // Make sure that there are 2 entries associated with id=1
        var mapOfLists = JoinUtils.groupCollection(new JoinKey("Id"), JoinUtils.EXCHANGE_MAIN, result);
        assertEquals("There should be 2 records associated with Id=1", 2, mapOfLists.get("1").size());
        assertNull("There should not be a record with id=4", mapOfLists.get("4"));

        // Test results
        assertEquals("Result size should be 4", 4, result.size());

        // Test keys
        for (var map : result) {
            verifyFields(map);
            verifyCommonFields(map);

        }
    }

    @Test
    public void testLeftJoin() {
        var joinProcessor = getProcessor(JoinType.LEFT);

        var result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        // Make sure that there are 2 entries associated with id=1
        var mapOfLists = JoinUtils.groupCollection(new JoinKey("Id"), JoinUtils.EXCHANGE_MAIN, result);
        assertEquals("There should be 2 records associated with Id=1", 2, mapOfLists.get("1").size());
        assertNull("There should not be a record with id=4", mapOfLists.get("4"));

        // Test results
        assertEquals("Result size should be 5", 5, result.size());

        // Test keys
        for (var map : result) {
            verifyFields(map);

            verifyCommonFields(map);
            switch (map.get("Id")) {
                case "5":
                    assertEquals("Name=Eddie", "Eddie", map.get("Name"));
                    assertEquals("Pet=\"\"", "", map.get("Pet"));
                    assertEquals("Color=\"\"", "", map.get("Color"));
                    break;
            }
        }
    }

    @Test
    public void testRightJoin() {
        var joinProcessor = getProcessor(JoinType.RIGHT);

        var result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        // Make sure that there are 2 entries associated with id=1
        var mapOfLists = JoinUtils.groupCollection(new JoinKey("Id"), JoinUtils.EXCHANGE_MAIN, result);
        assertEquals("There should be 2 records associated with Id=1", 2, mapOfLists.get("1").size());
        assertNotNull("There should be a record with id=4", mapOfLists.get("4"));
        assertNull("There should not be a record with id=5", mapOfLists.get("5"));

        // Test results
        assertEquals("Result size should be 5", 5, result.size());

        // Test keys
        for (var map : result) {
            verifyFields(map);

            verifyCommonFields(map);
            switch (map.get("Id")) {
                case "4":
                    assertEquals("Name=\"\"", "", map.get("Name"));
                    assertEquals("Pet=Iguana", "Iguana", map.get("Pet"));
                    assertEquals("Color=Orange", "Orange", map.get("Color"));
                    break;
            }
        }
    }

    private JoinCollectionsProcessor getProcessor(JoinType joinType) {
        return new JoinCollectionsProcessor(
                List.of(new JoinKey("Id", "Id")),
                joinType,
                ResultType.AS_IS,
                new Fields(field("Id"), field("Name")),
                new Fields(field("Pet"), field("Color")));
    }

    private void verifyCommonFields(Map<String, String> map) {
        switch(map.get("Id")) {
            case "1": // There should be 2 records with Id=1
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
        }
    }

    private void verifyFields(Map<String, String> map) {
        assertTrue("Keys contains Id", map.keySet().contains("Id"));
        assertTrue("Keys contains Name", map.keySet().contains("Name"));
        assertTrue("Keys contains Pet", map.keySet().contains("Pet"));
        assertTrue("Keys contains Color", map.keySet().contains("Color"));

        // Make sure we didn't get these
        assertFalse("Keys does not contain Age", map.keySet().contains("Age"));
    }


}