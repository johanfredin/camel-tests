package se.fredin.llama.examples;

import org.apache.camel.Exchange;
import se.fredin.llama.LlamaRoute;
import se.fredin.llama.examples.jaxb.Users;
import se.fredin.llama.utils.Endpoint;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Doing ex.1 where input/output is xml
 */
public class Ex1_XML extends LlamaRoute {

    @Override
    public void configure() {
        from(Endpoint.file(prop("ex-input-directory"), "foo.xml"))
        .convertBodyTo(Users.class)
        .process(this::processUsers)
        .marshal().jaxb()
        .to(Endpoint.file(prop("ex-output-directory"), "foo_fixed.xml"))
        .end();
    }

    private void processUsers(Exchange exchange) {
        Users users = exchange.getIn().getBody(Users.class);
        users.setUsers(users.getUser()
                .stream()                                                       // Iterate users
                .filter(user -> user.getAge() > 0 && user.getAge() < 100)       // Filter out invalid age
                .sorted(Comparator.comparing(Users.User::getCountry))           // Sort on country
                .peek(user -> user.setGender(user.getGender().toUpperCase()))   // Set gender to be uppercase
                .collect(Collectors.toList()));                                 // Collect the update

        // Update the body
        exchange.getIn().setBody(users);
    }

}
