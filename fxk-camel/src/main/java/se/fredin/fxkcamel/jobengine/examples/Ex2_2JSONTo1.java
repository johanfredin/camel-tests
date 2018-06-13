package se.fredin.fxkcamel.jobengine.examples;

import org.apache.camel.Exchange;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import se.fredin.fxkcamel.jobengine.JobUtils;
import se.fredin.fxkcamel.jobengine.JobengineJob;
import se.fredin.fxkcamel.jobengine.bean.Pet;
import se.fredin.fxkcamel.jobengine.bean.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Read 2 csv files and join them into one
 */
public class Ex2_2JSONTo1 extends JobengineJob {


    @Override
    public void configure() throws Exception {

        ListJacksonDataFormat jsonFormatUser = new ListJacksonDataFormat(User.class);
        ListJacksonDataFormat jsonFormatPet = new ListJacksonDataFormat(Pet.class);


        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "pet.json"))
                .routeId("pets")
                .unmarshal(jsonFormatPet)
                .to("direct:pet")
                .setStartupOrder(1);

        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "person.json"))
                .routeId("users")
                .unmarshal(jsonFormatUser)
                .pollEnrich("direct:pet", (oe, ne) -> aggregate(oe, ne))
                .marshal().json(JsonLibrary.Jackson)
                .to(JobUtils.file(getSettingsComponent().getOutputDirectory(), "person-with-pets.json"))
                .setStartupOrder(2);

    }


    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        List<User> users = JobUtils.asList(oldExchange);
        List<Pet> pets = JobUtils.asList(newExchange);

        // Create map <k, list<v>> of pets
        Map<Long, List<Pet>> petMap = pets.stream()
                .collect(Collectors.groupingBy(Pet::getId));

        // Match the 2
        for (User u : users) {
            List<Pet> petList = petMap.get(u.getId());
            if (petList != null) {
                u.setPets(petList);
            }
        }

        oldExchange.getIn().setBody(users);
        return oldExchange;
    }

}
