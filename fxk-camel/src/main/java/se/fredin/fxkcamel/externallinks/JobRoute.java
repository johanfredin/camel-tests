package se.fredin.fxkcamel.externallinks;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import se.fredin.fxkcamel.externallinks.bean.Item;
import se.fredin.fxkcamel.externallinks.bean.ItemAsset;
import se.fredin.fxkcamel.jobengine.JobengineJob;
import se.fredin.fxkcamel.jobengine.task.TaskCall;
import se.fredin.fxkcamel.jobengine.task.group.GroupExternalLinksTask;
import se.fredin.fxkcamel.jobengine.task.join.OutEntity;
import se.fredin.fxkcamel.jobengine.task.join.RecordSelection;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static se.fredin.fxkcamel.jobengine.utils.JobUtils.file;

//@Component
public class JobRoute extends JobengineJob {

    private BindyCsvDataFormat assetFormat = new BindyCsvDataFormat(ItemAsset.class);
    private BindyCsvDataFormat itemFormat = new BindyCsvDataFormat(Item.class);

    @Override
    public void configure() {

        final String INPUT_DIR = "input-directory";
        final String OUTPUT_DIR = "output-directory";

        from(file(prop(INPUT_DIR), "items.csv"))                             // Read items.csv file from input directory
                .routeId("read-items")                                               // Name your route
                .unmarshal(itemFormat)                                               // Transform file to list of Item objects
                .split(body())                                                       // Split collection
                    .choice()                                                        // Specify condition
                        .when(simple("${in.body.isSentToWeb()}"))              // isSentToWeb() is a boolean method in the Item.java class
                            .to("seda:items-ok-split")                               // When true send to this route
                        .otherwise()                                                 // Else
                            .to("seda:items-nok")                                    // Send to this route
                .startupOrder(1);                                                    // Set this route to start first

        from("seda:items-ok-split")                                             // Read the items that are sent to web based on condition in route 1
                .routeId("aggregate-items-ok-split")                                 // Name the route
                .aggregate(constant(true), TaskCall::union)                    // Aggregate all the items that gets sent one by one from route1 into a collection again
                .completionInterval(500L)                                            // Not yet sure why this is needed
                .to("seda:items-ok-aggregated")                                      // Send the collection to items-ok-aggregated endpoint
                .startupOrder(2);                                                    // Set this route to start second

        from("seda:items-nok")                                                 // Read the items that are not send to web based on condition in route 1
                .routeId("read-items-nok")                                          // Name the route
                .aggregate(constant(true), TaskCall::union)                   // Aggregate all items that gets sent one by one from route1 into a collection again
                .completionInterval(500L)                                           // Not yet sure why this is needed
                .process(this::clearPaths)                                          // Invoke our own method that clears the image paths in the Item bean
                .to("seda:items-nok-grouped")                                       // Send the collection to items-nok-grouped endpoint
                .startupOrder(3);                                                   // Set this route to start third


        from(file(prop(INPUT_DIR), "item-assets.csv"))                      // Read item-assets.csv from input directory
                .routeId("read-item-assets")                                        // Name the route
                .unmarshal(assetFormat)                                             // Turn file into a collection of ItemAsset beans
                .process(this::filterAssets)                                        // Invoke our own method that filters the assets
                .pollEnrich(                                                        // Enrich route with content from another route
                        "seda:items-ok-aggregated",                      // Name of the route we want to join in
                        (me, ne) -> TaskCall.join(                                  // Call our own join class
                                me,                                                 // The main exchange (this route)
                                ne,                                                 // The joining exchange (items-ok-aggregated)
                                RecordSelection.RECORDS_ONLY_IN_TYPE_1_AND_2,       // Join condition. In this case the id must exist in both routes
                                OutEntity.ENTITY_1                                  // We only want data from the main exchange
                        )
                )
                .process(e -> new GroupExternalLinksTask(e).doExecuteTask())          // Group the data calling our own group class pasing in the exchange (collection of ItemAsset beans)
                .pollEnrich("seda:items-nok-grouped", TaskCall::union)     // Union our collection (that is now an Item collection) with the Item collection from the route items-nok-grouped
                .marshal(itemFormat)                                                 // Marshal the collection into csv-format
                .to(file(prop(OUTPUT_DIR), "items-external-links.csv"))      // Write the csv to file items-external-links.csv in the output directory
                .startupOrder(4);                                                    // Set this route to start last

    }

    private void filterAssets(Exchange e) {
        List<String> imageTypes = Arrays.asList("01", "02", "03");
        List<ItemAsset> assets = JobUtils.<ItemAsset>asFxkBeanList(e)
                .stream()
                .filter(a -> {
                            if (!a.getUncPath().isEmpty()) {
                                try {
                                    String[] typeTokens = a.getType().split("-");
                                    if (typeTokens.length > 1) {
                                        return imageTypes.contains(typeTokens[0]) && !a.getQuality().equalsIgnoreCase("originalimage");
                                    }
                                } catch (Exception ex) {
                                    return false;
                                }
                            }

                            return false;
                        }
                )
                .peek(asset -> asset.setUncPath(JobUtils.getTransformedUrl(asset.getUncPath(), prop("imm-url-prefix"), prop("ecom-url-prefix"))))
                .collect(Collectors.toList());
        e.getIn().setBody(assets);
    }

    private void clearPaths(Exchange exchange) {
        exchange.getIn().setBody(JobUtils.<Item>asFxkBeanList(exchange)
                .stream()
                .peek(Item::clearLinks)
                .collect(Collectors.toList()));
    }

}
