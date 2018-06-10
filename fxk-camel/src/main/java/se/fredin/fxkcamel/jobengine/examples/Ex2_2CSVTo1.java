package se.fredin.fxkcamel.jobengine.examples;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;
import se.fredin.fxkcamel.jobengine.JobUtils;
import se.fredin.fxkcamel.jobengine.JobengineJob;
import se.fredin.fxkcamel.jobengine.bean.Pet;
import se.fredin.fxkcamel.jobengine.bean.User;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Read 2 csv files and join them into one
 */
@Component
public class Ex2_2CSVTo1 extends JobengineJob {


    @Override
    public void configure() throws Exception {


        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "person.csv"))
                .unmarshal(new BindyCsvDataFormat(User.class))
                .pollEnrich("direct:pet", new AggregationStrategy() {
                    @Override
                    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                        List<User> users = JobUtils.asList(oldExchange);
                        List<Pet> pets = JobUtils.asList(newExchange);

                        // Create map <k, list<v>> of pets
                        Map<Long, List<Pet>> petMap = pets
                                .stream()
                                .collect(Collectors.groupingBy(Pet::getId));

                        // Match the 2
                        for(User u: users) {
                            List<Pet> petList = petMap.get(u.getId());
                            if(petList != null) {
                                u.setPets(petList);
                            }
                        }

                        oldExchange.getIn().setBody(users);
                        return oldExchange;
                    }
                })
                .marshal(new BindyCsvDataFormat(User.class))
                .to(JobUtils.file(getSettingsComponent().getOutputDirectory(), "person-with-pets.csv"));

        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "pet.csv"))
                .unmarshal(new BindyCsvDataFormat(Pet.class))
                .to("direct:pet");
    }
}
