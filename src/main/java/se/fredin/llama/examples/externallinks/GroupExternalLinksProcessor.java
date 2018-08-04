package se.fredin.llama.examples.externallinks;

import org.apache.camel.Exchange;
import se.fredin.llama.examples.externallinks.bean.Item;
import se.fredin.llama.examples.externallinks.bean.ItemAsset;
import se.fredin.llama.processor.group.GroupProcessor;

import java.util.List;
import java.util.Map;

public class GroupExternalLinksProcessor extends GroupProcessor<ItemAsset, Item> {

    public GroupExternalLinksProcessor(Exchange exchange) {
        super(exchange);
    }

    @Override
    protected Item getResult(Map.Entry<Object, List<ItemAsset>> entry) {
        Item item = new Item();
        item.setArticleNumber(entry.getKey().toString());
        for (ItemAsset asset : entry.getValue()) {
            switch (asset.getType().toLowerCase()) {
                case "03-tillagnings bild":
                    item.setCookingImage(asset.getUncPath());
                    break;
                case "20-säkerhetsdatablad":
                    item.setSafetySheet(asset.getUncPath());
                    break;
                case "01-produkt bild":
                    item.setProductImage(asset.getUncPath());
                    break;
                case "02-förpacknings bild":
                    item.setPackagingImage(asset.getUncPath());
                    break;
            }
        }
        return item;
    }

}
