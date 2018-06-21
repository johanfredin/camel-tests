package se.fredin.fxkcamel.externallinks;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.stereotype.Component;
import se.fredin.fxkcamel.externallinks.bean.Item;
import se.fredin.fxkcamel.externallinks.bean.ItemAsset;
import se.fredin.fxkcamel.externallinks.bean.ItemExternalLinks;
import se.fredin.fxkcamel.jobengine.JobengineJob;
import se.fredin.fxkcamel.jobengine.task.TaskCall;
import se.fredin.fxkcamel.jobengine.task.join.OutEntity;
import se.fredin.fxkcamel.jobengine.task.join.RecordSelection;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static se.fredin.fxkcamel.jobengine.utils.JobUtils.file;

@Component
public class JobRoute extends JobengineJob {

    private BindyCsvDataFormat assetFormat = new BindyCsvDataFormat(ItemAsset.class);
    private BindyCsvDataFormat itemFormat = new BindyCsvDataFormat(Item.class);
    private BindyCsvDataFormat externalLinksFormat = new BindyCsvDataFormat(ItemExternalLinks.class);

    @Override
    public void configure() {

        final String INPUT_DIR = "input-directory";
        final String OUTPUT_DIR = "output-directory";


        from(file(prop(INPUT_DIR), "items.csv")).routeId("read-items")
                .unmarshal(itemFormat)
                .split(body())
                .choice()
                .when(simple("${in.body.isSentToWeb()}"))
                .to("seda:items-ok-split")
                .otherwise()
                .to("seda:items-nok")
                .startupOrder(1);

        from("seda:items-ok-split").routeId("aggregate-items-ok-split")
                .aggregate(constant(true), (oe, ne) -> TaskCall.union(oe, ne))
                .completionInterval(500L)
                .to("seda:items-ok-aggregated")
                .startupOrder(2);

        from("seda:items-nok")
                .aggregate(constant(true), (oe, ne) -> TaskCall.union(oe, ne))
                .completionInterval(500L)
                .marshal(itemFormat)
                .to(file(prop(OUTPUT_DIR), "items-nok.csv"))
                .startupOrder(3);


        from(file(prop(INPUT_DIR), "item-assets.csv")).routeId("read-item-assets")
                .unmarshal(assetFormat)
                .process(this::filterAssets)
                .pollEnrich(
                        "seda:items-ok-aggregated",
                        (oe, ne) -> TaskCall.join(oe, ne, RecordSelection.RECORDS_ONLY_IN_TYPE_1_AND_2, OutEntity.ENTITY_1)
                )
                .process(this::groupRecords)
                .marshal(externalLinksFormat)
                .to(file(prop("output-directory"), "items-external-links.csv"))
                .setStartupOrder(4);


    }

    private void filterAssets(Exchange e) {
        List<String> imageTypes = Arrays.asList("01", "02", "03");
        List<ItemAsset> assets = JobUtils.<ItemAsset>asList(e)
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

    private void groupRecords(Exchange e) {
        Map<Object, List<ItemAsset>> assets = JobUtils.asMap(e);

        List<ItemExternalLinks> externalLinks = new ArrayList<>();

        for (Map.Entry<Object, List<ItemAsset>> entry : assets.entrySet()) {
            ItemExternalLinks externalLink = new ItemExternalLinks(entry.getKey().toString());
            for (ItemAsset asset : entry.getValue()) {
                switch (asset.getType().toLowerCase()) {
                    case "03-tillagnings bild":
                        externalLink.setCookingImage(asset.getUncPath());
                        break;
                    case "20-säkerhetsdatablad":
                        externalLink.setSafetySheet(asset.getUncPath());
                        break;
                    case "01-produkt bild":
                        externalLink.setProductImage(asset.getUncPath());
                        break;
                    case "02-förpacknings bild":
                        externalLink.setPackagingImage(asset.getUncPath());
                        break;
                }
                externalLinks.add(externalLink);
            }
        }
        e.getIn().setBody(externalLinks);
    }

}
