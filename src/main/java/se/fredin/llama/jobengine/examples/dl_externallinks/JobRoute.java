package se.fredin.llama.jobengine.examples.dl_externallinks;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import se.fredin.llama.jobengine.JobengineJob;
import se.fredin.llama.jobengine.bean.FxKBean;
import se.fredin.llama.jobengine.examples.dl_externallinks.bean.*;
import se.fredin.llama.jobengine.utils.JobUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JobRoute extends JobengineJob {

    private final String INPUT_DIR = "input-directory-dl";

    @Override
    public void configure() {

        // Read sub-entities into collections
        String readAssetRoute = getRoute("read-assets", "product-assets.csv", ProductAsset.class, "assets", 1);
        String readBrandsRoute = getRoute("read-brands", "product-brand.csv", ProductLabel.class, "brands", 2);
        String readProductItemRelationsRoute = getRoute("read-product-items", "product-item-relations.csv", ProductItemRelation.class, "product-items", 3);
        String readLabelsRoute = getRoute("read-product-labels", "product-markningar.csv", ProductBrand.class, "product-labels", 4);

        // Read main route and join in sub-entities
        from(JobUtils.file(prop(INPUT_DIR), "product-langs.csv"))
                .routeId("read-products")
                .unmarshal(new BindyCsvDataFormat(Product.class))

                .pollEnrich(readAssetRoute, (me, ne) -> addSubEntity(me, ne, ProductAsset.class))
                .pollEnrich(readBrandsRoute, (me, ne) -> addSubEntity(me, ne, ProductLabel.class))
                .pollEnrich(readProductItemRelationsRoute, (me, ne) -> addSubEntity(me, ne, ProductItemRelation.class))
                .pollEnrich(readLabelsRoute, (me, ne) -> addSubEntity(me, ne, ProductBrand.class))
                .marshal().json(JsonLibrary.Jackson)
                .to(JobUtils.file(prop("output-directory-dl"), "products.json"))
                .startupOrder(5);

    }

    private <T extends FxKBean> Exchange addSubEntity(Exchange mainExchange, Exchange subEntityExchange, Class<T> subEntityToAdd) {
        Map<Object, List<Product>> products = JobUtils.asMap(mainExchange);
        Map<Object, List<T>> subEntitiesMap = JobUtils.asMap(subEntityExchange);

        // Find a match
        for (Object key : products.keySet()) {
            List<T> subEntities = subEntitiesMap.get(key);
            if (subEntities != null) {
                List<Product> productList = products.get(key.toString());
                for (Product p : productList) {
                    if (subEntityToAdd.isInstance(ProductAsset.class)) {
                        p.setAssets((List<ProductAsset>) subEntities);
                    } else if (subEntityToAdd.isInstance(ProductBrand.class)) {
                        p.setBrands((List<ProductBrand>) subEntities);
                    } else if (subEntityToAdd.isInstance(ProductLabel.class)) {
                        p.setLabels((List<ProductLabel>) subEntities);
                    } else if (subEntityToAdd.isInstance(ProductItemRelation.class)) {
                        p.setItems((List<ProductItemRelation>) subEntities);
                    }
                }
            }
        }

        List<Product> mapToList = products.entrySet()
                .stream()
                .flatMap(objectListEntry -> objectListEntry.getValue().stream())
                .collect(Collectors.toList());
        mainExchange.getIn().setBody(mapToList);
        return mainExchange;
    }

    public String getRoute(String routeId, String fileName, Class clazz, String endpoint, int startupOrder) {
        from(JobUtils.file(prop(INPUT_DIR), fileName))
                .routeId(routeId)
                .unmarshal(new BindyCsvDataFormat(clazz))
                .to("seda:" + endpoint)
                .startupOrder(startupOrder);

        return "seda:" + endpoint;
    }
}
