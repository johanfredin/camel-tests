package se.fredin.fxkcamel.jobengine.examples;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import se.fredin.fxkcamel.jobengine.JobengineJob;
import se.fredin.fxkcamel.jobengine.examples.bean.Pet;
import se.fredin.fxkcamel.jobengine.examples.bean.User;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Read 2 csv files and join them into one json file
 */
public class Ex2_2CSVTo1JSON extends JobengineJob {


    @Override
    public void configure() throws Exception {

        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "pet.csv"))
                .routeId("pets")
                .unmarshal(new BindyCsvDataFormat(Pet.class))
                .to("direct:pet")
                .setStartupOrder(1);

        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "person.csv"))
                .routeId("users")
                .unmarshal(new BindyCsvDataFormat(User.class))
                .pollEnrich("direct:pet", (oe, ne) -> aggregate(oe, ne))
                .marshal().json(JsonLibrary.Jackson)
                .to(JobUtils.file(getSettingsComponent().getOutputDirectory(), "person-with-pets.json"))
                .setStartupOrder(2);

    }


    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        // Create map <k, list<v>> of pets
        Map<Long, List<Pet>> petMap = JobUtils.<Pet>asFxkBeanList(newExchange).stream()
                .collect(Collectors.groupingBy(Pet::getId));


        // Match the 2 (JRE8 way)
        List<User> users = JobUtils.<User>asFxkBeanList(oldExchange)
                .stream()
                .peek(u -> u.setPets(petMap.get(u.getId())))
                .collect(Collectors.toList());

        oldExchange.getIn().setBody(users);
        return oldExchange;
    }

}
