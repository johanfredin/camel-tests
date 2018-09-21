package se.fredin.llama.processor;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.utils.ProcessorUtils;

import java.util.List;

public abstract class SimpleProcessor<T extends LlamaBean> extends BaseProcessor {

    protected Exchange exchange;

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
        var beans = ProcessorUtils.<T>asLlamaBeanList(this.exchange);
        this.exchange.getIn().setBody(transformData(beans));
        postExecute();
        return this.exchange;
    }

    public abstract List<T> transformData(List<T> beans);

    @Override
    protected Exchange result() {
        return this.exchange;
    }
}
