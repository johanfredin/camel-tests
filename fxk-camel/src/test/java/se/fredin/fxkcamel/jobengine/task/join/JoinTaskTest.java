package se.fredin.fxkcamel.jobengine.task.join;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.fredin.fxkcamel.jobengine.TestFixture.*;

public class JoinTaskTest {

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
        JoinTask joinTask = new JoinTask();
        joinTask.setJoinType(JoinType.INNER);
        joinTask.setEntity1Fields("Id", "Name");
        joinTask.setEntity2Fields("Pet", "Color");
        joinTask.setJoinKeys(Arrays.asList(new JoinKey("Id", "Id")));

        List<Map<String, String>> result = joinTask.join(this.mainEntries, this.joiningEntries);
        assertEquals("Result size should be 3", 3, result.size());
        for(Map<String, String> map : result) {
            assertTrue("Keys contains Id", map.keySet().contains("Id"));
            assertTrue("Keys contains Name", map.keySet().contains("Name"));
            assertTrue("Keys contains Pet", map.keySet().contains("Pet"));
            assertTrue("Keys contains Color", map.keySet().contains("Color"));
        }
    }


}