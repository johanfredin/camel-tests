package se.fredin.llama.examples;

import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import se.fredin.llama.LlamaRoute;
import se.fredin.llama.examples.bean.Pet;
import se.fredin.llama.examples.bean.User;
import se.fredin.llama.utils.Endpoint;
import se.fredin.llama.utils.LlamaUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Read 2 csv files and filterValidateAgainst them into one
 */
public class Ex2_2CSVTo1 extends LlamaRoute {

    @Override
    public void configure() {

        from(Endpoint.file(prop("ex-input-directory"), "pet.csv"))
                .unmarshal(new BindyCsvDataFormat(Pet.class))
                .to("direct:pet")
                .startupOrder(1);

        from(Endpoint.file(prop("ex-input-directory"), "person.csv"))
                .unmarshal(new BindyCsvDataFormat(User.class))
                .pollEnrich("direct:pet", (oldExchange, newExchange) -> {
                    List<User> users = LlamaUtils.asLlamaBeanList(oldExchange);
                    List<Pet> pets = LlamaUtils.asLlamaBeanList(newExchange);

                    // Create map <k, list<v>> of pets
                    var petMap = pets
                            .stream()
                            .collect(Collectors.groupingBy(Pet::getId));

                    // Match the 2
                    users.forEach(u -> {
                        var petList = petMap.get(u.getId());
                        if (petList != null) {
                            u.setPets(petList);
                        }
                    });

                    oldExchange.getIn().setBody(users);
                    return oldExchange;
                })
                .marshal(new BindyCsvDataFormat(User.class))
                .to(Endpoint.file(prop("ex-output-directory"), "person-with-pets.csv"))
                .startupOrder(2);


    }
}
