package se.fredin.llama.utils;

import org.junit.Test;
import se.fredin.llama.TestFixture;

import static org.junit.Assert.*;

public class LlamaUtilsTest {

    @Test
    public void testIsTrueAny() {
        assertTrue("Any is true", LlamaUtils.isTrueAny(true, false, false));
        assertFalse("None is not true", LlamaUtils.isTrueAny(false, false, false));
    }

    @Test
    public void testIsTrueAll() {
        assertTrue("All is true", LlamaUtils.isTrueAll(true, true, true));
        assertFalse("Is not true all", LlamaUtils.isTrueAll(true, true, false));
    }

    @Test
    public void testIsTrue() {
        assertTrue("All is true", LlamaUtils.isTrue("&&", true, true, true));
        assertFalse("Is not true all", LlamaUtils.isTrue("&&",true, true, false));

        assertTrue("Any is true", LlamaUtils.isTrue("||", true, false, false));
        assertFalse("None is not true", LlamaUtils.isTrue("||", false, false, false));
    }

    @Test
    public void testGetTransformedUrl() {
        var transformedUrl = LlamaUtils.getTransformedUrl(
                "d:/files/some/directory/myImage.jpg",
                "D:\\files\\some\\DiRectory\\",
                "http://www.outurl.com/"
        );
        assertEquals("Out url=http://www.outurl.com/myImage.jpg", "http://www.outurl.com/myimage.jpg", transformedUrl);
    }

    @Test
    public void testGetMergedMap() {
        var mergedMap = LlamaUtils.getMergedMap(TestFixture.mainEntries.get(0), TestFixture.joiningEntries.get(0));
        assertEquals("Merged amount of entries=5", 5, mergedMap.entrySet().size());
    }


    @Test
    public void testWithinRange() {
    }

    @Test
    public void testIsHeader() {
    }
}