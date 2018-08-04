package se.fredin.llama.examples;

import se.fredin.llama.FxKJobTest;

public class Ex1_CSVTest extends FxKJobTest {

//    @Override
//    protected List<MockRouteDefinition> getMockRoutes() {
//        return Arrays.asList(
//                new MockRouteDefinition("read-foo-csv",
//                        Arrays.asList(
//                                new EndpointToMockEndpoint(
//                                        ProcessorUtils.file(getSettingsComponent().getOutputDirectory(), "foo_fixed.csv"),
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