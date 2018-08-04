package se.fredin.llama.task.join;

import org.junit.Before;
import org.junit.Test;
import se.fredin.llama.TestFixture;
import se.fredin.llama.jobengine.task.Fields;
import se.fredin.llama.jobengine.task.join.JoinKey;
import se.fredin.llama.jobengine.task.join.JoinTask;
import se.fredin.llama.jobengine.task.join.JoinType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static se.fredin.llama.jobengine.utils.TaskUtils.field;
import static se.fredin.llama.jobengine.utils.TaskUtils.fields;

public class JoinTaskTest {

    private List<Map<String, String>> mainEntries;
    private List<Map<String, String>> joiningEntries;

    @Before
    public void innit() {
        this.mainEntries = TestFixture.getList(
                TestFixture.getTestObjects(TestFixture.kvp("Id", "1"), TestFixture.kvp("Name", "Jonas"), TestFixture.kvp("Age", "25")),
                TestFixture.getTestObjects(TestFixture.kvp("Id", "2"), TestFixture.kvp("Name", "Leslie"), TestFixture.kvp("Age", "30")),
                TestFixture.getTestObjects(TestFixture.kvp("Id", "3"), TestFixture.kvp("Name", "Nils"), TestFixture.kvp("Age", "12"))
        );
        this.joiningEntries = TestFixture.getList(
                TestFixture.getTestObjects(TestFixture.kvp("Id", "1"), TestFixture.kvp("Pet", "Dog"), TestFixture.kvp("Color", "Blue")),
                TestFixture.getTestObjects(TestFixture.kvp("Id", "2"), TestFixture.kvp("Pet", "Cat"), TestFixture.kvp("Color", "Green")),
                TestFixture.getTestObjects(TestFixture.kvp("Id", "3"), TestFixture.kvp("Pet", "Lizard"), TestFixture.kvp("Color", "Yellow"))
        );
    }

    @Test
    public void testInnerJoin() {
        JoinTask joinTask = new JoinTask();
        joinTask.setJoinType(JoinType.INNER);
        joinTask.setEntity1Fields(new Fields(fields(field("Id"), field("Name"))));
        joinTask.setEntity2Fields(new Fields(fields(field("Pet"), field("Color"))));
        joinTask.setJoinKeys(Arrays.asList(new JoinKey("Id", "Id")));

        List<Map<String, String>> result = joinTask.join(this.mainEntries, this.joiningEntries);

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