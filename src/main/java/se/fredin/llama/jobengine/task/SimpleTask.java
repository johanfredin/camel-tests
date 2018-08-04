package se.fredin.llama.jobengine.task;

import org.apache.camel.Exchange;
import se.fredin.llama.jobengine.bean.FxKBean;
import se.fredin.llama.jobengine.utils.JobUtils;

import java.util.List;

public abstract class SimpleTask<T extends FxKBean> extends BaseTask {

    protected Exchange exchange;

    public SimpleTask(Exchange exchange) {
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
    public Exchange doExecuteTask() {
        List<T> beans = JobUtils.asFxkBeanList(this.exchange);
        this.exchange.getIn().setBody(transformData(beans));
        postExecute();
        return this.exchange;
    }

    public abstract List<T> transformData(List<T> beans);
}
