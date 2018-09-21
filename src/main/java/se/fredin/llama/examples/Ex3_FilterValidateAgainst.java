package se.fredin.llama.examples;

import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import se.fredin.llama.LlamaRoute;
import se.fredin.llama.examples.bean.CsvUser;
import se.fredin.llama.examples.bean.Pet;
import se.fredin.llama.processor.Processors;
import se.fredin.llama.processor.ResultType;
import se.fredin.llama.processor.join.JoinType;
import se.fredin.llama.utils.Endpoint;

public class Ex3_FilterValidateAgainst extends LlamaRoute {

    public void configure() {
        String petRoute = getRoute("read-pets", "pet.csv", Pet.class, "pets", 1);

        from(Endpoint.file(defaultInputDir(), "person.csv"))
                .routeId("read-persons")
                .unmarshal(new BindyCsvDataFormat(CsvUser.class))
                .pollEnrich(petRoute, (mainExchange, joiningExchange) -> Processors.<CsvUser, Pet>filterValidateAgainst(mainExchange, joiningExchange, JoinType.INNER, ResultType.LIST))
                .marshal(new BindyCsvDataFormat(CsvUser.class))
                .to(Endpoint.file(defaultOutputDir(), "person-validated.csv"))
                .startupOrder(2)
                .onCompletion().log("Done!");

    }


}
