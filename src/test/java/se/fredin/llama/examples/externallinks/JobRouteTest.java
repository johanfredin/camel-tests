package se.fredin.llama.examples.externallinks;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

//@ComponentScan
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class JobRouteTest extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new JobRoute();
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Before
    public void mockEndpoints() {
//        AdviceWithRouteBuilder mockEndpoint = new AdviceWithRouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                // Mock for testing
//                interceptSendToEndpoint("seda:items-ok-split").skipSendToOriginalEndpoint().to("mock:items-ok-split");
//                interceptSendToEndpoint("seda:items-nok-split").skipSendToOriginalEndpoint().to("mock:items-nok-split");
//            }
//        };
//        context.getRouteDefinition("read-items").adviceWith(context, mockEndpoint);
    }

    @Test
    public void testReadItemsRoute() {
//        context.start();
//        System.out.println("read");
//        context.stop();
    }


}