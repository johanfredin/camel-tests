package se.fredin.llama.processor.join;

import org.junit.Before;
import org.junit.Test;
import se.fredin.llama.processor.Fields;
import se.fredin.llama.processor.ResultType;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


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

    /*
     *  Test an inner join with the field collections.
     */
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
        result.forEach(map -> {
            verifyFields(map);
            verifyCommonFields(map);
        });
    }

    /*
    Test an inner join where we have multiple keys
     */
    @Test
    public void testInnerJoinMultipleKeys() {
        var main = List.of(
                Map.of("Id", "1", "Name", "Anders", "Age", "15"),
                Map.of("Id", "1", "Name", "Anders", "Age", "12"));
        var joining = List.of(
                Map.of("Id", "1", "Name", "Anders", "Profession", "Developer"),
                Map.of("Id", "1", "Name", "Lena", "Profession", "Scrum Master"));

        var joinProcessor = getProcessor(JoinType.INNER, JoinUtils.joinKeys("Id", "Name"));
        joinProcessor.setEntity1Fields(Fields.ALL);
        joinProcessor.setEntity2Fields(Fields.ALL);
        var result = joinProcessor.join(main, joining);

        // Joining on Id and Name should result in 2 entries
        assertEquals("There should be 2 entries in the result when using Id and Name as keys", 2, result.size());

        // The record with name=Lena should not exist now
        result.forEach(m -> {
            assertNotEquals("There should not be a Lena in here", "Lena", m.get("Name"));
            assertEquals("There should only be developer proffessions here", "Developer", m.get("Profession"));
        });
    }

    /*
    Test the left join
     */
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
        result.forEach(map -> {
            verifyFields(map);

            verifyCommonFields(map);
            switch (map.get("Id")) {
                case "5":
                    assertEquals("Name=Eddie", "Eddie", map.get("Name"));
                    assertEquals("Pet=\"\"", "", map.get("Pet"));
                    assertEquals("Color=\"\"", "", map.get("Color"));
                    break;
            }
        });
    }

    /*
    Test the right join
     */
    @Test
    public void testRightJoin() {
        var joinProcessor = getProcessor(JoinType.RIGHT, JoinUtils.joinKeys("Id"), JoinUtils.createFields("Name"), JoinUtils.createFields("Id", "Pet", "Color"));

        var result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        // Make sure that there are 2 entries associated with id=1
        var mapOfLists = JoinUtils.groupCollection(new JoinKey("Id"), JoinUtils.EXCHANGE_MAIN, result);
        assertEquals("There should be 2 records associated with Id=1", 2, mapOfLists.get("1").size());
        assertNotNull("There should be a record with id=4", mapOfLists.get("4"));
        assertNull("There should not be a record with id=5", mapOfLists.get("5"));

        // Test results
        assertEquals("Result size should be 5", 5, result.size());

        // Test keys
        result.forEach(map -> {
            verifyFields(map);

            verifyCommonFields(map);
            switch (map.get("Id")) {
                case "4":
                    assertEquals("Id=4", "4", map.get("Id"));
                    assertEquals("Name=\"\"", "", map.get("Name"));
                    assertEquals("Pet=Iguana", "Iguana", map.get("Pet"));
                    assertEquals("Color=Orange", "Orange", map.get("Color"));
                    break;
            }
        });
    }

    /*
    Test a left excluding join.
     */
    @Test
    public void testLeftExcludingJoin() {
        var joinProcessor = getProcessor(JoinType.LEFT_EXCLUDING);
        var result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        assertEquals("There should only be 1 record where the key does not exist in the other", 1, result.size());

        // Test keys
        result.forEach(map -> {
            verifyFields(map, joinProcessor.getEntity1Fields(), joinProcessor.getEntity2Fields());
            assertEquals("Id=5", "5", map.get("Id"));
            assertEquals("Name=Eddie", "Eddie", map.get("Name"));
        });
    }

    /*
    Test right excluding join
     */
    @Test
    public void testRightExcludingJoin() {
        var joinProcessor = getProcessor(JoinType.RIGHT_EXCLUDING);
        var result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        assertEquals("There should only be 1 record where the key does not exist in the other", 1, result.size());

        // Test keys
        result.forEach(map -> {
            /*
             * Pass in the fields expected to exist and not to exist in reverse order here
             * since they will have been swapped when the join is of type right_excluding.
             */
            verifyFields(map, joinProcessor.getEntity2Fields(), joinProcessor.getEntity1Fields());

            // Only one entry in map
            assertEquals("Pet=Iguana", "Iguana", map.get("Pet"));
            assertEquals("Color=Orange", "Orange", map.get("Color"));
        });
    }

    /*
    Redo the same logic as in the first testInnerJoin method
    except specify new output field names
     */
    @Test
    public void testInnerJoinNewOutputFieldNames() {
        var joinProcessor = getProcessor(
                JoinType.INNER,
                JoinUtils.joinKeys("Id"),
                JoinUtils.createFields(Map.of("Id", "Identifier", "Name", "User Name")),
                JoinUtils.createFields(Map.of("Pet", "Animal", "Color", "Color")));

        var result = joinProcessor.join(this.mainEntries, this.joiningEntries);

        // Verify join logic works just as well when there are optional output field names.
        var mapOfLists = JoinUtils.groupCollection(new JoinKey("Identifier"), JoinUtils.EXCHANGE_MAIN, result);
        assertEquals("There should be 2 records associated with Identifier=1", 2, mapOfLists.get("1").size());
        assertNull("There should not be a record with Identifier=4", mapOfLists.get("4"));

        // Test results
        assertEquals("Result size should be 4", 4, result.size());

        result.forEach(map -> {
            verifyFields(map,
                    JoinUtils.createFields("Identifier", "User Name", "Animal", "Color"), // Verify fields are renamed
                    JoinUtils.createFields("Id", "Name", "Pet"));   // Verify original field names no longer exist

            // Verify logic is the same with different output field names as before
            switch (map.get("Identifier")) {
                case "1": // There should be 2 records with Id=1
                    assertTrue("User Name=Jonas||Lena", map.get("User Name").equals("Jonas") || map.get("User Name").equals("Lena"));
                    break;
                case "2":
                    assertEquals("Use Name=Leslie", "Leslie", map.get("User Name"));
                    assertEquals("Animal=Cat", "Cat", map.get("Animal"));
                    assertEquals("Color=Green", "Green", map.get("Color"));
                    break;
                case "3":
                    assertEquals("User Name=Nils", "Nils", map.get("User Name"));
                    assertEquals("Animal=Lizard", "Lizard", map.get("Animal"));
                    assertEquals("Color=Yellow", "Yellow", map.get("Color"));
            }
        });
    }

    /*
    Test an inner join with Fields.ALL specified for both the collections
     */
    @Test
    public void testInnerJoinAllFields() {
        var main = List.of(
                Map.of("Id", "1", "First Name", "Lars"),
                Map.of("Id", "2", "First Name", "Bert")
        );

        var joining = List.of(
                Map.of("Id", "1", "Last Name", "Larsson"),
                Map.of("Id", "2", "Last Name", "Karlsson")
        );

        var joinProcessor = getProcessor(JoinType.INNER, JoinUtils.joinKeys("Id"), Fields.ALL, Fields.ALL);
        var result = joinProcessor.join(main, joining);

        assertEquals("Result size=2", 2, result.size());

        // Verify all fields were added (Id overridden)
        result.forEach(map -> verifyFields(map, JoinUtils.createFields("Id", "First Name", "Last Name"), JoinUtils.createFields()));
    }

    /*
    Test an inner join with Fields.ALL from main collection and Fields.NONE from joining collection
     */
    @Test
    public void testInnerJoinOnlyFieldsFromMain() {
        var main = List.of(
                Map.of("Id", "1", "First Name", "Lars"),
                Map.of("Id", "2", "First Name", "Bert")
        );

        var joining = List.of(
                Map.of("Id", "1", "Last Name", "Larsson", "Age", "25"),
                Map.of("Id", "2", "Last Name", "Karlsson", "Age", "25")
        );

        var joinProcessor = getProcessor(JoinType.INNER, JoinUtils.joinKeys("Id"), Fields.ALL, Fields.NONE);
        var result = joinProcessor.join(main, joining);

        assertEquals("Result size=2", 2, result.size());

        // Verify no fields from entity 2 added (minus Id)
        result.forEach(map -> verifyFields(map, JoinUtils.createFields("Id", "First Name"), JoinUtils.createFields("Last Name", "Age")));
    }

    /*
    Test an inner join with Fields.NONE from main collection and Fields.ALL from joining collection.
     */
    @Test
    public void testInnerJoinOnlyFieldsFromJoining() {
        var main = List.of(
                Map.of("Id", "1", "First Name", "Lars"),
                Map.of("Id", "2", "First Name", "Bert")
        );

        var joining = List.of(
                Map.of("Id", "1", "Last Name", "Larsson", "Age", "25"),
                Map.of("Id", "2", "Last Name", "Karlsson", "Age", "25")
        );

        var joinProcessor = getProcessor(JoinType.INNER, JoinUtils.joinKeys("Id"), Fields.NONE, Fields.ALL);
        var result = joinProcessor.join(main, joining);

        assertEquals("Result size=2", 2, result.size());

        result.forEach(map -> verifyFields(map, JoinUtils.createFields("Id", "Last Name", "Age"), JoinUtils.createFields("First Name")));
    }

    /*
    Test an inner join where we are matching on a key that that has different names in the collections.
     */
    @Test
    public void testInnerJoinDifferentKeyNames() {
        var main = List.of(
                Map.of("Id", "1", "First Name", "Lars"),
                Map.of("Id", "2", "First Name", "Bert")
        );

        var joining = List.of(
                Map.of("Identifier", "1", "Last Name", "Larsson", "Age", "25"),
                Map.of("Identifier", "2", "Last Name", "Karlsson", "Age", "25")
        );

        var joinProcessor = getProcessor(JoinType.INNER, JoinUtils.joinKeys(Map.of("Id", "Identifier")), Fields.ALL, Fields.ALL);
        var result = joinProcessor.join(main, joining);

        assertEquals("Result size=2", 2, result.size());

        result.forEach(map -> verifyFields(map, JoinUtils.createFields("Id", "First Name", "Identifier", "Last Name"), JoinUtils.createFields()));
    }

    /*
    Test an inner join with multiple keys where they all have different names in the collections.
     */
    @Test
    public void testInnerJoinMultipleKeysDifferentKeyNames() {
        var main = List.of(
                Map.of("Id", "1", "Name", "Lars"),
                Map.of("Id", "2", "Name", "Bert")
        );

        var joining = List.of(
                Map.of("Identifier", "1", "Nombre", "Lars", "Age", "25"),
                Map.of("Identifier", "2", "Nombre", "Bert", "Age", "25"),
                Map.of("Identifier", "3", "Nombre", "Karlsson", "Age", "35")
        );

        var joinProcessor = getProcessor(JoinType.INNER, JoinUtils.joinKeys(Map.of("Id", "Identifier", "Name", "Nombre")), Fields.ALL, Fields.ALL);
        var result = joinProcessor.join(main, joining);

        assertEquals("Result size=2", 2, result.size());

        result.forEach(map -> verifyFields(map, JoinUtils.createFields("Id", "Name", "Identifier", "Nombre", "Age"), JoinUtils.createFields()));
    }

    /*
    Test an inner join with multiple keys where they all have different names in the collections
    and we want the output field names to be different.
     */
    @Test
    public void testInnerJoinMultipleKeysDifferentKeyNamesAndNewOutputFieldNames() {
        var main = List.of(
                Map.of("Id", "1", "Name", "Lars"),
                Map.of("Id", "2", "Name", "Bert")
        );

        var joining = List.of(
                Map.of("Identifier", "1", "Nombre", "Lars", "Age", "25"),
                Map.of("Identifier", "2", "Nombre", "Bert", "Age", "25"),
                Map.of("Identifier", "3", "Nombre", "Karlsson", "Age", "35")
        );

        var joinProcessor = getProcessor(
                JoinType.INNER,
                JoinUtils.joinKeys(Map.of("Id", "Identifier", "Name", "Nombre")),
                JoinUtils.createFields(Map.of("Id", "ID", "Name", "NAME")),
                JoinUtils.createFields(Map.of("Identifier", "IDENTIFIER", "Nombre", "NOMBRE", "Age", "AGE")));
        var result = joinProcessor.join(main, joining);

        assertEquals("Result size=2", 2, result.size());

        result.forEach(map ->
                verifyFields(map,
                        JoinUtils.createFields("ID", "NAME", "IDENTIFIER", "NOMBRE", "AGE"),
                        JoinUtils.createFields("Id", "Name", "Identifier", "Nombre", "Age")));
    }

    private JoinCollectionsProcessor getProcessor(JoinType joinType) {
        return getProcessor(joinType, JoinUtils.joinKeys("Id"));
    }

    private JoinCollectionsProcessor getProcessor(JoinType joinType, List<JoinKey> joinKeys) {
        return getProcessor(joinType, joinKeys, JoinUtils.createFields("Id", "Name"), JoinUtils.createFields("Pet", "Color"));
    }

    /**
     * @param joinType what type of join this is
     * @param joinKeys the keys to join on
     * @param mainFields the fields we want out from the main collection.
     * @param joiningFields the fields we want from the joining collection.
     * @return a new join processor instance with the passed in params
     */
    private JoinCollectionsProcessor getProcessor(JoinType joinType, List<JoinKey> joinKeys, Fields mainFields, Fields joiningFields) {
        return new JoinCollectionsProcessor(
                joinKeys,
                joinType,
                ResultType.AS_IS,
                mainFields,
                joiningFields);
    }

    private void verifyCommonFields(Map<String, String> map) {
        switch (map.get("Id")) {
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

    /**
     * Expects fields <b>Id, Name, Pet, Color</b> to exist and field <b>Age</b> to not exist in passed in map
     * @param map map to verify
     */
    private void verifyFields(Map<String, String> map) {
        verifyFields(map, JoinUtils.createFields("Id", "Name", "Pet", "Color"), JoinUtils.createFields("Age"));
    }

    /**
     * Helper method to verify the fields returned from a join.
     * @param mapWithTheFields the map that contains with the fields to verify
     * @param fieldsToCheckExistsInMap the fields we expect to exist as keys in the passed in map.
     * @param fieldsToVerifyDoesNotExistInMap the fields we expect to not exist as keys in the passed map
     */
    private void verifyFields(Map<String, String> mapWithTheFields, Fields fieldsToCheckExistsInMap, Fields fieldsToVerifyDoesNotExistInMap) {
        fieldsToCheckExistsInMap.getFields()
                .forEach(e -> assertTrue("Map contains key=" + e.getOutName(), mapWithTheFields.containsKey(e.getOutName())));

        fieldsToVerifyDoesNotExistInMap.getFields()
                .forEach(e -> assertFalse("Map does not contains key=" + e.getOutName(), mapWithTheFields.containsKey(e.getOutName())));

    }


}