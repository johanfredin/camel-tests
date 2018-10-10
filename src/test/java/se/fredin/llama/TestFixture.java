package se.fredin.llama;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.bean.MockItem;
import se.fredin.llama.bean.MockItemAsset;
import se.fredin.llama.mock.exchange.MockExchange;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestFixture {

    public static List<Map<String, String>> mainEntries = List.of(
            Map.of("Id", "1", "Name", "Jonas", "Age", "25"),
            Map.of("Id", "1", "Name", "Lena", "Age", "26"),
            Map.of("Id", "2", "Name", "Leslie", "Age", "30"),
            Map.of("Id", "3", "Name", "Nils", "Age", "12"),
            Map.of("Id", "5", "Name", "Eddie", "Age", "56")
    );

    public static List<Map<String, String>> joiningEntries = List.of(
            Map.of("Id", "1", "Pet", "Dog", "Color", "Blue"),
            Map.of("Id", "2", "Pet", "Cat", "Color", "Green"),
            Map.of("Id", "3", "Pet", "Lizard", "Color", "Yellow"),
            Map.of("Id", "4", "Pet", "Iguana", "Color", "Orange")
    );

    public static final String DEFAULT_ITEM_NO = "Abc123";
    public static final String DEFAULT_NAME = "Samsung Galaxy S8";
    public static final String DEFAULT_QUALITY = "Thumbnail";
    public static final String DEFAULT_PATH = "http://www.somepage/someasset.jpg";

    public static MockItem getMockItem() {
        return getMockItem(DEFAULT_ITEM_NO, DEFAULT_NAME);
    }

    public static MockItem getMockItem(String itemNo, String name) {
        return new MockItem(itemNo, name);
    }

    public static MockItemAsset getMockItemAsset() {
        return getMockItemAsset(DEFAULT_ITEM_NO, DEFAULT_QUALITY, DEFAULT_PATH);
    }

    public static MockItemAsset getMockItemAsset(String itemNo, String quality, String uncPath) {
        return new MockItemAsset(itemNo, quality, uncPath);
    }

    public static List<MockItem> getMockItems(MockItem... mockItems) {
        return Arrays.asList(mockItems);
    }

    public static List<MockItemAsset> getMockItemAssets(MockItemAsset... mockItemAssets) {
        return Arrays.asList(mockItemAssets);
    }

    public static <T extends LlamaBean> Exchange getMockExchange(List<T> mockBody) {
        var mockExchange = new MockExchange();
        mockExchange.getIn().setBody(mockBody);
        return mockExchange;
    }


}
