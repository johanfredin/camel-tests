package se.fredin.llama;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import se.fredin.llama.utils.Endpoint;

public abstract class LlamaRoute extends RouteBuilder {

    public String prop(String property) {
        return "{{" + property + "}}";
    }

    public String defaultInputDir() {
        return prop(Endpoint.INPUT_DIR);
    }

    public String defaultOutputDir() {
        return prop(Endpoint.OUTPUT_DIR);
    }
}


