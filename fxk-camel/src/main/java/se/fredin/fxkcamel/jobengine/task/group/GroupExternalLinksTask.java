package se.fredin.fxkcamel.jobengine.task.group;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.externallinks.bean.Item;
import se.fredin.fxkcamel.externallinks.bean.ItemAsset;

import java.util.List;
import java.util.Map;

public class GroupExternalLinksTask extends GroupTask<ItemAsset, Item> {

    public GroupExternalLinksTask(Exchange exchange) {
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
