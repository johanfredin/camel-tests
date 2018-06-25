package se.fredin.fxkcamel.jobengine.examples;

import se.fredin.fxkcamel.jobengine.JobengineJob;

/**
 * Read 2 csv files and join them into one
 */
public class Ex2_2CSVTo1 extends JobengineJob {


    @Override
    public void configure() throws Exception {

//
//        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "person.csv"))
//                .unmarshal(new BindyCsvDataFormat(User.class))
//                .pollEnrich("direct:pet", new AggregationStrategy() {
//                    @Override
//                    public Exchange union(Exchange oldExchange, Exchange newExchange) {
//                        List<User> users = JobUtils.asFxkBeanList(oldExchange);
//                        List<Pet> pets = JobUtils.asFxkBeanList(newExchange);
//
//                        // Create map <k, list<v>> of pets
//                        Map<Long, List<Pet>> petMap = pets
//                                .stream()
//                                .collect(Collectors.groupingBy(Pet::getId));
//
//                        // Match the 2
//                        for(User u: users) {
//                            List<Pet> petList = petMap.get(u.getId());
//                            if(petList != null) {
//                                u.setPets(petList);
//                            }
//                        }
//
//                        oldExchange.getIn().setBody(users);
//                        return oldExchange;
//                    }
//                })
//                .marshal(new BindyCsvDataFormat(User.class))
//                .to(JobUtils.file(getSettingsComponent().getOutputDirectory(), "person-with-pets.csv"));
//
//        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "pet.csv"))
//                .unmarshal(new BindyCsvDataFormat(Pet.class))
//                .to("direct:pet");
    }
}
