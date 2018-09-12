package se.fredin.llama.examples;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import se.fredin.llama.LlamaRoute;
import se.fredin.llama.examples.bean.Pet;
import se.fredin.llama.examples.bean.User;
import se.fredin.llama.utils.Endpoint;
import se.fredin.llama.utils.ProcessorUtils;

import java.util.stream.Collectors;

/**
 * Read 2 csv files and join them into one json file
 */
public class Ex2_2CSVTo1JSON extends LlamaRoute {


    @Override
    public void configure() {

        from(Endpoint.file(prop("ex-input-directory"), "pet.csv"))
                .routeId("pets")
                .unmarshal(new BindyCsvDataFormat(Pet.class))
                .to("direct:pet")
                .setStartupOrder(1);

        from(Endpoint.file(prop("ex-input-directory"), "person.csv"))
                .routeId("users")
                .unmarshal(new BindyCsvDataFormat(User.class))
                .pollEnrich("direct:pet", this::aggregate)
                .marshal().json(JsonLibrary.Jackson)
                .to(Endpoint.file(prop("ex-output-directory"), "person-with-pets.json"))
                .setStartupOrder(2);

    }


    private Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        // Create map <k, list<v>> of pets
        var petMap = ProcessorUtils.<Pet>asLlamaBeanList(newExchange).stream()
                .collect(Collectors.groupingBy(Pet::getId));


        // Match the 2 (JRE8 way)
        var users = ProcessorUtils.<User>asLlamaBeanList(oldExchange)
                .stream()
                .peek(u -> u.setPets(petMap.get(u.getId())))
                .collect(Collectors.toList());

        oldExchange.getIn().setBody(users);
        return oldExchange;
    }

}
