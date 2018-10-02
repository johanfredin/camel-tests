package se.fredin.llama.processor;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.utils.LlamaUtils;

import java.util.List;

/**
 * Superclass for "simple" processors that modifies a singe exchange.
 * @param <T> any class extending {@link LlamaBean}
 */
public abstract class SimpleProcessor<T extends LlamaBean> extends BaseProcessor {

    protected Exchange exchange;

    /**
     * Create a new instance calling super first.
     * @param exchange the exchange to process.
     */
    public SimpleProcessor(Exchange exchange) {
        super();
        setExchange(exchange);
    }

    protected void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Exchange getExchange() {
        return exchange;
    }

    @Override
    public Exchange doExecuteProcess() {
        var beans = LlamaUtils.<T>asLlamaBeanList(this.exchange);
        this.exchange.getIn().setBody(transformData(beans));
        postExecute();
        return this.exchange;
    }

    public abstract List<T> transformData(List<T> beans);

}
