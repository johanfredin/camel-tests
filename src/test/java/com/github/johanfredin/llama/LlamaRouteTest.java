package com.github.johanfredin.llama;

import com.github.johanfredin.llama.bean.LlamaBean;
import com.github.johanfredin.llama.utils.InputOptions;
import com.github.johanfredin.llama.utils.InputType;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LlamaRouteTest {

    private static final String DIR = "/some/directory";
    private static final String FILE_NAME = "fileName.file";
    private static final String SQL = "select * from my_table";
    private static final String OUTPUT_CLASS = "com.github.johanfredin.llama.bean.LlamaBean";

    /*
    Create a new instance doing nothing with the method, we only want to test the helper methods
     */
    private LlamaRoute llamaRoute = new LlamaRoute() {
        public void configure() {
        }
    };

    @Test
    public void nextAvailableStartup() {
        LlamaRoute.startupOrder = 10;
        LlamaRoute.nextAvailableStartup();
        assertEquals("Startup order now=11", 11, LlamaRoute.startupOrder);
    }

    @Test
    public void prop() {
        String myProp = "myProp";
        assertEquals("Prop call={{myProp}}", "{{myProp}}", llamaRoute.prop(myProp));
    }

    @Test
    public void getRoute() {
        var route = llamaRoute.getRoute("my_route_id", DIR, FILE_NAME, LlamaBean.class, "my_endpoint", 1, true);
        assertEquals("Route id=my_route_id", "my_route_id", route.getId());
        assertEquals("Startup order=1", 1, route.getStartupOrder().intValue());
        assertEquals("Autostart=true", "true", route.getAutoStartup());
    }

    @Test
    public void getRoute1() {
        var route = llamaRoute.getRoute("my_route_id", DIR, FILE_NAME, "my_endpoint", 1, true);
        assertEquals("Route id=my_route_id", "my_route_id", route.getId());
        assertEquals("Startup order=1", 1, route.getStartupOrder().intValue());
        assertEquals("Autostart=true", "true", route.getAutoStartup());
    }


    @Test
    public void csvToCollectionOfMaps() {
        var format = llamaRoute.csvToCollectionOfMaps();
        assertFormat(format, true, ';', false, '"', true, null, false, "", false);
    }

    @Test
    public void csvToCollectionOfMaps1Param() {
        var format = llamaRoute.csvToCollectionOfMaps('|');
        assertFormat(format, true, '|', false, '"', true, null, false, "", false);
    }

    @Test
    public void csvToCollectionOfMaps2Params() {
        var format = llamaRoute.csvToCollectionOfMaps('|', false);
        assertFormat(format, false, '|', false, '"', true, null, false, "", false);
    }

    @Test
    public void csvToCollectionOfMapsAllParams() {
        var format = llamaRoute.csvToCollectionOfMaps(false, '!', false, '&', false, 'T', true, "NULL", false);
        assertFormat(format, false, '!', false, '&', false, 'T', true, "NULL", false);
    }

    private void assertFormat(CsvDataFormat format, boolean isOrderedMaps, Character delimiter, boolean allowMissingColumnNames,
                              Character escapeChar, boolean ignoreEmptyLines, Character quoteChar, boolean skipHeaderRecord,
                              String nullValue, boolean ignoreSurroundingSpaces) {

        assertEquals("Ordered maps=" + isOrderedMaps, isOrderedMaps, format.isUseOrderedMaps());
        assertEquals("Delimeter=" + delimiter, delimiter, format.getDelimiter());
        assertEquals("Allow missing column names=" + allowMissingColumnNames, allowMissingColumnNames, format.getAllowMissingColumnNames());
        assertEquals("Escape char=" + escapeChar, escapeChar, format.getEscape());
        assertEquals("Ignore empty lines=" + ignoreEmptyLines, ignoreEmptyLines, format.getIgnoreEmptyLines());
        assertEquals("Quote char=" + quoteChar, quoteChar, format.getQuote());
        assertEquals("Skip header record=" + skipHeaderRecord, skipHeaderRecord, format.getSkipHeaderRecord());
        assertEquals("Null value=" + nullValue, nullValue, format.getNullString());
        assertEquals("Ignore surrounding spaces=" + ignoreSurroundingSpaces, ignoreSurroundingSpaces, format.getIgnoreSurroundingSpaces());

    }

    @Test
    public void testFile2Params() {
        String fileUrl = llamaRoute.file(DIR, FILE_NAME);
        assertEquals("file:" + DIR + "/?fileName=" + FILE_NAME + "&noop=true", fileUrl);
    }

    @Test
    public void testFile3Params() {
        String fileUrl = llamaRoute.file(DIR, FILE_NAME, InputOptions.DISCARD);
        assertEquals("file:" + DIR + "/?fileName=" + FILE_NAME + "&noop=false", fileUrl);
    }

    @Test
    public void testFileSource() {
        String fileUrl = llamaRoute.fileSource(InputType.JPA, DIR, FILE_NAME, InputOptions.KEEP);
        assertEquals("jpa:" + DIR + "/?fileName=" + FILE_NAME + "&noop=true", fileUrl);
    }

    @Test
    public void testSqlToJPA2Params() {
        String sqlUri = llamaRoute.sql(SQL, LlamaBean.class);
        assertEquals("sql:" + SQL + "?outputClass=" + OUTPUT_CLASS + "&noop=true&useIterator=false&routeEmptyResultSet=true", sqlUri);
    }

    @Test
    public void testSqlToJPA3Params() {
        String sqlUri = llamaRoute.sql(SQL, LlamaBean.class, false);
        assertEquals("sql:" + SQL + "?outputClass=" + OUTPUT_CLASS + "&noop=true&useIterator=false&routeEmptyResultSet=false", sqlUri);
    }

    @Test
    public void testSqlToJPA5Params() {
        String sqlUri = llamaRoute.sql(SQL, LlamaBean.class, true, true, false);
        assertEquals("sql:" + SQL + "?outputClass=" + OUTPUT_CLASS + "&noop=true&useIterator=true&routeEmptyResultSet=false", sqlUri);
    }

    @Test
    public void testSqlToList1Param() {
        String sqlUri = llamaRoute.sql(SQL);
        assertEquals("sql:" + SQL + "?noop=true&useIterator=false&routeEmptyResultSet=true", sqlUri);
    }

    @Test
    public void testSqlToList2Params() {
        String sqlUri = llamaRoute.sql(SQL, true);
        assertEquals("sql:" + SQL + "?noop=true&useIterator=false&routeEmptyResultSet=true", sqlUri);
    }

    @Test
    public void testSqlToList4Params() {
        String sqlUri = llamaRoute.sql(SQL, false, true, false);
        assertEquals("sql:" + SQL + "?noop=false&useIterator=true&routeEmptyResultSet=false", sqlUri);
    }

    @Test
    public void testControlBus1Param() {
        String controlBusUri = llamaRoute.controlBus("my_route_id");
        assertEquals("controlbus:route?routeId=my_route_id&action=stop&async=true", controlBusUri);
    }

    @Test
    public void testControlBus2Params() {
        String controlBusUri = llamaRoute.controlBus("my_route_id", "do_something");
        assertEquals("controlbus:route?routeId=my_route_id&action=do_something&async=true", controlBusUri);
    }
}