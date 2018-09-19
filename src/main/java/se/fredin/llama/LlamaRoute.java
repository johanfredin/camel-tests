package se.fredin.llama;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.stereotype.Component;
import se.fredin.llama.utils.Endpoint;

public abstract class LlamaRoute extends RouteBuilder {

    protected String prop(String property) {
        return "{{" + property + "}}";
    }

    protected String defaultInputDir() {
        return prop(Endpoint.INPUT_DIR);
    }

    protected String defaultOutputDir() {
        return prop(Endpoint.OUTPUT_DIR);
    }

    protected String getRoute(String routeId, String fileName, Class clazz, String endpoint, int startupOrder) {
        return getRoute(routeId, defaultInputDir(), fileName, clazz, endpoint, startupOrder);
    }

    protected String getRoute(String routeId, String directory, String fileName, Class clazz, String endpoint, int startupOrder) {
        from(Endpoint.file(prop(directory), fileName))
                .routeId(routeId)
                .unmarshal()
                .bindy(BindyType.Csv, clazz)
                .to("seda:" + endpoint)
                .startupOrder(startupOrder);

        return "seda:" + endpoint;
    }
}


