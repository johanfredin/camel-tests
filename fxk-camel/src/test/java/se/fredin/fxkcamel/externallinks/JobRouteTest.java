package se.fredin.fxkcamel.externallinks;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@ComponentScan
@RunWith(SpringRunner.class)
@SpringBootTest
public class JobRouteTest extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new JobRoute();
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Before
    public void mockEndpoints() throws Exception {
        AdviceWithRouteBuilder mockEndpoint = new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Mock for testing
                interceptSendToEndpoint("seda:items-ok-split").skipSendToOriginalEndpoint().to("mock:items-ok-split");
                interceptSendToEndpoint("seda:items-nok-split").skipSendToOriginalEndpoint().to("mock:items-nok-split");
            }
        };
        context.getRouteDefinition("read-items").adviceWith(context, mockEndpoint);
    }

    @Test
    public void testReadItemsRoute() throws Exception {
        context.start();
        System.out.println("read");
        context.stop();
    }


}