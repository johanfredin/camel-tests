package se.fredin.llama.processor.join;

import org.junit.Test;
import se.fredin.llama.TestFixture;
import se.fredin.llama.bean.MockItem;
import se.fredin.llama.bean.MockItemAsset;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JoinLlamaBeansProcessorTest {

    private static final String MUTUAL_ID = TestFixture.getMockItem().getId();
    private static final String UNIQUE_ID_ITEMS = "UNIQUE_ID_ITEMS";
    private static final String UNIQUE_ID_ITEM_ASSETS = "UNIQUE_ID_ITEM_ASSETS";

    private Map<Object, List<MockItem>> mockItems = Map.of(
            MUTUAL_ID, List.of(TestFixture.getMockItem(), TestFixture.getMockItem(MUTUAL_ID, "Iphone")),
            UNIQUE_ID_ITEMS, List.of()
    );

    private Map<Object, List<MockItemAsset>> mockItemAssets = Map.of(
            MUTUAL_ID, List.of(TestFixture.getMockItemAsset()),
            UNIQUE_ID_ITEM_ASSETS, List.of(TestFixture.getMockItemAsset(UNIQUE_ID_ITEM_ASSETS, "Bad Quality", "Bad Path"))
    );

    @Test
    public void testInnerJoinExchange1() {
        var joinBeansProcessor = getProcessor(JoinType.INNER, OutData.EXCHANGE_1);
        Map<Object, List<MockItem>> resultMap = joinBeansProcessor.join(mockItems, mockItemAssets);

        assertFalse("ResultMap should not contain entry with key=" + UNIQUE_ID_ITEMS, resultMap.keySet().contains(UNIQUE_ID_ITEMS));
        assertTrue("ResultMap contains all values associated with id", resultMap.get(MUTUAL_ID).size() == mockItems.get(MUTUAL_ID).size());
    }

    @Test
    public void testInnerJoinExchange2() {
        // Stupid assert but just for verify
        assertEquals("mock assets size=2", 2, mockItemAssets.size());

        var joinBeansProcessor = getProcessor(JoinType.INNER, OutData.EXCHANGE_2);
        Map<Object, List<MockItemAsset>> resultMap = joinBeansProcessor.join(mockItems, mockItemAssets);

        assertFalse("ResultMap should NOT have same amount of entries as mockItems map", resultMap.size() == mockItemAssets.size());
        assertTrue("ResultMap contains key=" + MUTUAL_ID, resultMap.keySet().contains(MUTUAL_ID));
        assertFalse("ResultMap does not contains key=" + UNIQUE_ID_ITEM_ASSETS, resultMap.keySet().contains(UNIQUE_ID_ITEM_ASSETS));
    }

    @Test
    public void testLeftJoinExchange1() {

        var joinBeansProcessor = getProcessor(JoinType.LEFT_EXCLUDING, OutData.EXCHANGE_1);
        Map<Object, List<MockItem>> resultMap = joinBeansProcessor.join(mockItems, mockItemAssets);

        assertFalse("ResultMap should NOT have same amount of entries as mockItems map", resultMap.size() == mockItems.size());
        assertTrue("ResultMap contains key=" + UNIQUE_ID_ITEMS, resultMap.keySet().contains(UNIQUE_ID_ITEMS));
        assertFalse("ResultMap does not contains key=" + MUTUAL_ID, resultMap.keySet().contains(MUTUAL_ID));
    }

    @Test
    public void testLeftJoinExchange2() {

        var joinBeansProcessor = getProcessor(JoinType.LEFT_EXCLUDING, OutData.EXCHANGE_2);
        Map<Object, List<MockItemAsset>> resultMap = joinBeansProcessor.join(mockItems, mockItemAssets);

        assertFalse("ResultMap should NOT have same amount of entries as mockItems map", resultMap.size() == mockItemAssets.size());
        assertTrue("ResultMap contains key=" + UNIQUE_ID_ITEM_ASSETS, resultMap.keySet().contains(UNIQUE_ID_ITEM_ASSETS));
        assertFalse("ResultMap does not contains key=" + MUTUAL_ID, resultMap.keySet().contains(MUTUAL_ID));
    }

    @Test
    public void testRightJoinExchange1() {

        var joinBeansProcessor = getProcessor(JoinType.RIGHT_EXCLUDING, OutData.EXCHANGE_1);
        Map<Object, List<MockItemAsset>> resultMap = joinBeansProcessor.join(mockItems, mockItemAssets);

        assertFalse("ResultMap should NOT have same amount of entries as mockItemAssetss map", resultMap.size() == mockItemAssets.size());
        assertTrue("ResultMap contains key=" + UNIQUE_ID_ITEM_ASSETS, resultMap.keySet().contains(UNIQUE_ID_ITEM_ASSETS));
        assertFalse("ResultMap does not contains key=" + MUTUAL_ID, resultMap.keySet().contains(MUTUAL_ID));
    }

    @Test
    public void testRightJoinExchange2() {

        var joinBeansProcessor = getProcessor(JoinType.RIGHT_EXCLUDING, OutData.EXCHANGE_2);
        Map<Object, List<MockItem>> resultMap = joinBeansProcessor.join(mockItems, mockItemAssets);

        assertFalse("ResultMap should NOT have same amount of entries as mockItems map", resultMap.size() == mockItems.size());
        assertTrue("ResultMap contains key=" + UNIQUE_ID_ITEMS, resultMap.keySet().contains(UNIQUE_ID_ITEMS));
        assertFalse("ResultMap does not contains key=" + MUTUAL_ID, resultMap.keySet().contains(MUTUAL_ID));
    }

    private JoinLlamaBeansProcessor getProcessor(JoinType type, OutData outData) {
        return new JoinLlamaBeansProcessor(type, outData);
    }

}