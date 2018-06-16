package se.fredin.fxkcamel.jobengine;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.stereotype.Component;
import se.fredin.fxkcamel.jobengine.bean.Item;
import se.fredin.fxkcamel.jobengine.bean.ItemAsset;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;
import se.fredin.fxkcamel.jobengine.utils.TaskUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static se.fredin.fxkcamel.jobengine.utils.JobUtils.*;

@Component
public class JobRoute extends JobengineJob {

    private final String INPUT_DIR = "input-directory";
    private final String OUTPUT_DIR = "output-directory";

    private BindyCsvDataFormat assetFormat = new BindyCsvDataFormat(ItemAsset.class);
    private BindyCsvDataFormat itemFormat = new BindyCsvDataFormat(Item.class);

    @Override
    public void configure() throws Exception {

        from(file(prop(INPUT_DIR), "item-assets.csv"))
                .routeId("read-item-assets")
                .unmarshal(assetFormat)
                .process(e -> filterAssets(e))
                .marshal(assetFormat)
                .to(file(prop(OUTPUT_DIR), "assets-filtered.csv"))
                .setStartupOrder(1);

        from(file(prop(INPUT_DIR), "items.csv"))
                .routeId("read-items")
                .unmarshal(itemFormat)
                .split(body())
                .choice()
                .when(simple("${in.body.isSentToWeb()}"))
                .to("seda:items-ok")
                .otherwise()
                .to("seda:items-nok")

//                .pollEnrich("direct:assets", )
                .startupOrder(2);

        from("seda:items-ok")
                .aggregate(constant(true), (oe, ne) -> TaskUtils.merge(oe, ne, Item.class))
                .completionInterval(500L)
                .marshal(itemFormat)
                .to(file(prop(OUTPUT_DIR), "items-ok.csv"))
                .startupOrder(3);

        from("seda:items-nok")
                .aggregate(constant(true), (oe, ne) -> TaskUtils.merge(oe, ne, Item.class))
                .completionInterval(500L)
                .marshal(itemFormat)
                .to(file(prop(OUTPUT_DIR), "items-nok.csv"))
                .startupOrder(4);


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
                                } catch(Exception ex) {
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

}
