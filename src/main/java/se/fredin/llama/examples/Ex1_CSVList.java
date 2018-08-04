package se.fredin.llama.examples;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import se.fredin.llama.LlamaRoute;
import se.fredin.llama.utils.Endpoint;
import se.fredin.llama.utils.ProcessorUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Perform ex_1 in this case with collections of lists as csv object
 * representations
 */
public class Ex1_CSVList extends LlamaRoute {

    @Override
    public void configure() {
        CsvDataFormat format = new CsvDataFormat();
        format.setDelimiter(';');
        format.setUseMaps(true);

        from(Endpoint.file(prop("ex-input-directory"), "foo.csv"))
                .routeId("read-csv")
                .unmarshal(format)
                .process(this::transformData)
                .marshal(format)
                .to(Endpoint.file(prop("ex-output-directory"), "foo-plain-modified.csv"));
    }

    private void transformData(Exchange exchange) {
        List<Map<String, String>> collect = ProcessorUtils.asList(exchange)
                .stream()
                .filter(e -> ProcessorUtils.withinRange(e.get("age"), 0, 100))
                .peek(e -> e.put("gender", e.get("gender").toUpperCase()))
                .sorted(Comparator.comparing(e -> e.get("country")))
                .collect(Collectors.toList());

        Map<String, String> header = new HashMap<>();
        for(String s : collect.get(0).keySet()) {
            header.put(s, s);
        }
        collect.add(0, header);
        exchange.getIn().setBody(collect);
    }

}
