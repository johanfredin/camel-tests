package se.fredin.llama.utils;

import org.junit.Test;
import se.fredin.llama.TestFixture;
import se.fredin.llama.pojo.Keys;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JoinUtilsTest {

    @Test
    public void testGroupCollection() {
        assertTrue("Param in is a list", TestFixture.mainEntries instanceof List);

        var groupedCollection = JoinUtils.groupCollection(Keys.of("Id"), TestFixture.mainEntries);
        assertTrue("Collection is not a map", groupedCollection instanceof Map);
        assertEquals("Map size=4", 4, groupedCollection.size());
        assertEquals("2 records where Id=1", 2, groupedCollection.get("1").size());
        assertEquals("1 records where Id=2", 1, groupedCollection.get("2").size());
        assertEquals("1 records where Id=3", 1, groupedCollection.get("3").size());
        assertEquals("1 records where Id=5", 1, groupedCollection.get("5").size());
    }

    @Test
    public void testKeysAsString() {
        var keysAsString = JoinUtils.keysAsString(TestFixture.mainEntries.get(0), Keys.of("Id", "Name", "Age"), JoinUtils.EXCHANGE_MAIN);
        assertEquals("Combined key length=", "1Jonas25".length(), keysAsString.length());
        assertTrue("Key contains 1", keysAsString.contains("1"));
        assertTrue("Key contains Jonas", keysAsString.contains("Jonas"));
        assertTrue("Key contains 25", keysAsString.contains("25"));
    }

    @Test
    public void testKeysAsStringKeyInJoining() {
        var keysAsString = JoinUtils.keysAsString(TestFixture.joiningEntries.get(0),
                Keys.of(Map.of(
                        "DummyKeyInMain", "Id",
                        "DummyPetInMain", "Pet",
                        "DummyColorInMain", "Color"
                )), JoinUtils.EXCHANGE_JOINING);
        
        assertEquals("Combined key length=", "1DogBlue".length(), keysAsString.length());
        assertTrue("Key contains 1", keysAsString.contains("1"));
        assertTrue("Key contains Dog", keysAsString.contains("Dog"));
        assertTrue("Key contains Blue", keysAsString.contains("Blue"));
    }

    @Test
    public void fetchHeader() {
    }

    @Test
    public void getFields() {
    }

    @Test
    public void createDummyMap() {
    }

    @Test
    public void createMergedMap() {
    }
}