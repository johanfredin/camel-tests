package se.fredin.llama.processor.group;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.BaseProcessor;
import se.fredin.llama.utils.ProcessorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class GroupProcessor<T extends LlamaBean, R extends LlamaBean> extends BaseProcessor {

    private Exchange exchange;
    private List<R> resultList;
    private Function<T, R> groupFunction;

    public GroupProcessor(Exchange exchange) {
        super();
        this.exchange = exchange;
    }

    @Override
    public Exchange doExecuteTask() {
        for (var entry : ProcessorUtils.<T>asMap(this.exchange).entrySet()) {
            addResult(entry);
        }

        this.exchange.getIn().setBody(this.resultList);
        return this.exchange;
    }

    public void addResult(Map.Entry<Object, List<T>> entry) {
        if (this.resultList == null) {
            this.resultList = new ArrayList<>();
        }
        this.resultList.add(getResult(entry));
    }

    protected abstract R getResult(Map.Entry<Object, List<T>> entry);

    @Override
    public String toString() {
        return "GroupProcessor{" +
                "exchange=" + exchange +
                ", resultList=" + resultList +
                ", groupFunction=" + groupFunction +
                '}';
    }
}
