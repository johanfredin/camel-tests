package se.fredin.llama.processor.generic;

import org.apache.camel.Exchange;
import org.apache.camel.language.Simple;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.BaseProcessor;
import se.fredin.llama.utils.LlamaUtils;

import java.util.List;
import java.util.Map;

/**
 * Superclass for "simple" processors that modifies a singe exchange.
 * This could very easily be achieved by calling .stream() on a collection
 * and SHOULD be the way to do it if we have several things we need to do (transform, sort, filter etc).
 * However if we know we simply want to do one thing to our collection and want to
 * have the code a bit less bloated then this could be a bit more elegant.
 * Body of the exchange is expected to contain a {@link List} of {@link java.util.Map}s.
 * @author JFN
 */
public abstract class SimpleGenericProcessor extends GenericProcessor {

    protected Exchange exchange;

    public SimpleGenericProcessor() {}

    /**
     * Create a new instance calling super first.
     * @param exchange the exchange to process.
     */
    public SimpleGenericProcessor(Exchange exchange) {
        setExchange(exchange);
    }

    /**
     * @param exchange the exchange to process
     */
    protected void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    /**
     * @return the exchange to process
     */
    public Exchange getExchange() {
        return exchange;
    }

    @Override
    public Exchange getResult() {
        return this.exchange;
    }

    @Override
    public void process() {
        var records = LlamaUtils.asLinkedListOfMaps(this.exchange);
        this.initialRecords = records.size();

        var modifiedRecords = processData(records);

        // Set the count before applying header to avoid confusion
        this.processedRecords = modifiedRecords.size();

        if (super.includeHeader) {
            modifiedRecords.add(0, getHeader(modifiedRecords.get(0).keySet()));
        }

        this.exchange.getIn().setBody(modifiedRecords);
    }

    /**
     * The result of this method will be what we give the exchange.
     * What we do with the passed in collection is decided in the subclasses that
     * will be forced to implement this method.
     * @param records the beans to modify
     * @return the passed in beans, modified.
     */
    public abstract List<Map<String, String>> processData(List<Map<String, String>> records);

}
