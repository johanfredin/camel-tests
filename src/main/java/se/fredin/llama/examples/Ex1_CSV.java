package se.fredin.llama.examples;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.stereotype.Component;
import se.fredin.llama.LlamaRoute;
import se.fredin.llama.examples.bean.CsvUser;
import se.fredin.llama.utils.Endpoint;
import se.fredin.llama.utils.LlamaUtils;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Doing ex.1 where input/output is csv
 */
@Component
public class Ex1_CSV extends LlamaRoute {

    @Override
    public void configure() {

        BindyCsvDataFormat bindyCsvDataFormat = new BindyCsvDataFormat(CsvUser.class);

        from(Endpoint.file(defaultInputDir(), "foo.csv"))                              // Fetch input file
                .routeId("read-foo-csv")
                .unmarshal(bindyCsvDataFormat)                                                  // Unmarshal CSV to POJO
                .process(this::processUsers)                                                    // Do transformation
                .marshal(bindyCsvDataFormat)                                                    // Marshal POJO back to CSV
                .to(Endpoint.file(defaultOutputDir(), "foo_fixed.csv"))                // Write output file
                .onCompletion().log("Done").end();
    }

    private void processUsers(Exchange exchange) {
        var users = LlamaUtils.<CsvUser>asLlamaBeanList(exchange)
                .stream()                                                                       // Iterate users
                .filter(user -> LlamaUtils.withinRange(user.getAge(), 0, 100))        // Filter out invalid age
                .sorted(Comparator.comparing(CsvUser::getCountry))                              // Sort on country
                .peek(user -> user.setGender(user.getGender().toUpperCase()))                   // Set gender to be uppercase
                .collect(Collectors.toList());                                                  // Collect the update

        // Update the body
        exchange.getIn().setBody(users);
    }


}
