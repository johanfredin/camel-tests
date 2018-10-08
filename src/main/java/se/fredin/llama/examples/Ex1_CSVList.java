package se.fredin.llama.examples;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.stereotype.Component;
import se.fredin.llama.LlamaRoute;
import se.fredin.llama.utils.Endpoint;
import se.fredin.llama.utils.LlamaUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Perform ex_1 in this case with collections of lists as csv object
 * representations
 */
@Component
public class Ex1_CSVList extends LlamaRoute implements LlamaExamples {

    @Override
    public void configure() {
        var format = new CsvDataFormat();
        format.setDelimiter(';');
        format.setUseMaps(true);

        from(Endpoint.file(exInputDir(), "foo.csv"))
                .routeId("read-csv")
                .unmarshal(format)
                .process(this::transformData)
                .marshal(format)
                .to(Endpoint.file(exOutputDir(), resultingFileName("csv")))
                .onCompletion().log(getCompletionMessage());
    }

    private void transformData(Exchange exchange) {
        var collect = LlamaUtils.asList(exchange)
                .stream()
                .filter(e -> LlamaUtils.withinRange(e.get("age"), 0, 100))
                .peek(e -> e.put("gender", e.get("gender").toUpperCase()))
                .sorted(Comparator.comparing(e -> e.get("country")))
                .collect(Collectors.toList());

        var header = new HashMap<String, String>();
        for (String s : collect.get(0).keySet()) {
            header.put(s, s);
        }
        collect.add(0, header);
        exchange.getIn().setBody(collect);
    }

    @Override
    public String exInputDir() {
        return prop("in-ex-1-csv-list");
    }

    @Override
    public String exOutputDir() {
        return prop("out-ex-1-csv-list");
    }
}
