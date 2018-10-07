package se.fredin.llama.examples;

import org.springframework.stereotype.Component;
import se.fredin.llama.LlamaRoute;
import se.fredin.llama.processor.Fields;
import se.fredin.llama.processor.Keys;
import se.fredin.llama.processor.Processors;
import se.fredin.llama.processor.ResultType;
import se.fredin.llama.processor.join.JoinType;
import se.fredin.llama.utils.Endpoint;

import java.util.Map;

@Component
public class Ex4_JoinCollections extends LlamaRoute {

    @Override
    public void configure() {
        var csvToMapsFormat = csvToCollectionOfMaps();
        var mapCollectionToFileFormat = csvToListOfLists();

        var petRoute = getRoute("pet-route", prop("input-directory"), "pet.csv", ResultType.MAP, "pets", nextAvailableStartup());

        from(Endpoint.file(prop("input-directory"), "person.csv"))
                .routeId("person-route")
                .unmarshal(csvToMapsFormat)
                .pollEnrich(petRoute, (me, je) -> Processors.join(me, je, Keys.of("id"), JoinType.INNER, Fields.ALL, Fields.of(Map.of("type", "animal")), ResultType.LIST))
                .marshal(mapCollectionToFileFormat)
                .to(Endpoint.file(prop("output-directory"), "join-collections-result.csv"))
                .startupOrder(nextAvailableStartup())
                .onCompletion().log("Person route is finished!");
    }
}
