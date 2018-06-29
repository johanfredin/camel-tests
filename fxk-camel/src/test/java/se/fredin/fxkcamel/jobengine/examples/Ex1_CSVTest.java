package se.fredin.fxkcamel.jobengine.examples;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import se.fredin.fxkcamel.jobengine.FxKJobTest;
import se.fredin.fxkcamel.jobengine.mock.EndpointToMockEndpoint;
import se.fredin.fxkcamel.jobengine.mock.MockRouteDefinition;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.Arrays;
import java.util.List;

public class Ex1_CSVTest extends FxKJobTest {

//    @Override
//    protected List<MockRouteDefinition> getMockRoutes() {
//        return Arrays.asList(
//                new MockRouteDefinition("read-foo-csv",
//                        Arrays.asList(
//                                new EndpointToMockEndpoint(
//                                        JobUtils.file(getSettingsComponent().getOutputDirectory(), "foo_fixed.csv"),
//                                        "mock:foo_fixed")
//                        )));
//    }
//
//    @Test
//    public void testRoute() throws Exception {
//        MockEndpoint mockEndpoint = getMockEndpoint("mock:foo_fixed");
//
//        context.start();
//        mockEndpoint.expectedMessageCount(1);
//        mockEndpoint.assertIsSatisfied();
//        context.stop();
//    }
}