package com.github.johanfredin.llama.processor.generic;

import com.github.johanfredin.llama.processor.BaseProcessor;

/**
 * Superclass for the processors working with generic collections instead of {@link com.github.johanfredin.llama.bean.LlamaBean}
 * collections. Holds only one property which is a boolean for determining if the header row should be returned as
 * the first entry in the resulting collection. By default this is false.
 * @author johan
 */
public abstract class GenericProcessor extends BaseProcessor {

    protected boolean includeHeader;

    protected GenericProcessor() {
        this(false);
    }

    /**
     * Create a new instance
     * @param includeHeader whether or not to include the header row (default is false)
     */
    protected GenericProcessor(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    /**
     * @return whether or not to include the header row (default is false)
     */
    public boolean isIncludeHeader() {
        return includeHeader;
    }

    /**
     * @param includeHeader whether or not to include the header row
     */
    public void setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    @Override
    public String toString() {
        return "GenericProcessor{" +
                "includeHeader=" + includeHeader +
                ", UN_ALTERED_VALUE=" + UN_ALTERED_VALUE +
                ", log=" + log +
                ", initialRecords=" + initialRecords +
                ", processedRecords=" + processedRecords +
                '}';
    }
}