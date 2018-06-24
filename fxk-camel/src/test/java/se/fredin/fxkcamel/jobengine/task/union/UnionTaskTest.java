package se.fredin.fxkcamel.jobengine.task.union;

import org.apache.camel.Exchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import se.fredin.fxkcamel.jobengine.TestFixture;
import se.fredin.fxkcamel.jobengine.bean.MockItem;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class UnionTaskTest {

    @Test
    public void testUnion() {
        MockItem mockItem1 = TestFixture.getMockItem();
        MockItem mockItem2 = TestFixture.getMockItem("CDE231", "Iphone");

        List<MockItem> itemsFirst = TestFixture.getMockItems(mockItem1);
        List<MockItem> itemsSecond = TestFixture.getMockItems(mockItem2);

        Exchange exchange1 = TestFixture.getMockExchange(itemsFirst);
        Exchange exchange2 = TestFixture.getMockExchange(itemsSecond);

        UnionTask unionTask = new UnionTask(exchange1, exchange2);
        Exchange union = unionTask.union();

        List<MockItem> result = JobUtils.<MockItem>asList(union);
        assertEquals("There should be 2 items in resulting list", 2, result.size());
        assertTrue("Result contains item1", result.contains(mockItem1));
        assertTrue("Result contains item2", result.contains(mockItem2));
    }

}
