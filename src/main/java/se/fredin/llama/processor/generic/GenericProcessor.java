package se.fredin.llama.processor.generic;

import se.fredin.llama.processor.BaseProcessor;

public abstract class GenericProcessor extends BaseProcessor {

    protected boolean includeHeader = false;

    public GenericProcessor(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    public boolean isIncludeHeader() {
        return includeHeader;
    }

    public void setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }
}
