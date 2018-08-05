package se.fredin.llama.examples;

import org.apache.camel.Exchange;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import se.fredin.llama.LlamaRoute;
import se.fredin.llama.examples.bean.CsvUser;
import se.fredin.llama.utils.Endpoint;
import se.fredin.llama.utils.ProcessorUtils;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Doing ex.1 where input/output is JSON
 */
public class Ex1_JSON extends LlamaRoute {

    @Override
    public void configure() {
        from(Endpoint.file(prop("ex-input-directory"), "foo.json"))
                .unmarshal(new ListJacksonDataFormat(CsvUser.class))
                .process(this::processUsers)
                .marshal().json(JsonLibrary.Jackson)
                .to(Endpoint.file(prop("ex-output-directory"), "foo_fixed.json"));
    }

    private void processUsers(Exchange exchange) {
        var users = ProcessorUtils.<CsvUser>asFxkBeanList(exchange)
                .stream()                                                       // Iterate users
                .filter(user -> user.getAge() > 0 && user.getAge() < 100)       // Filter out invalid age
                .sorted(Comparator.comparing(CsvUser::getCountry))              // Sort on country
                .peek(user -> user.setGender(user.getGender().toUpperCase()))   // Set gender to be uppercase
                .collect(Collectors.toList());                                  // Collect the update

        // Update the body
        exchange.getIn().setBody(users);
    }

}
