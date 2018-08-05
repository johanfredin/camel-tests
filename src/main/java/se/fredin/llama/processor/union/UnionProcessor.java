package se.fredin.llama.processor.union;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.BaseProcessor;
import se.fredin.llama.utils.ProcessorUtils;

import java.util.ArrayList;
import java.util.List;

public class UnionProcessor extends BaseProcessor {

    private Exchange newExchange;
    private Exchange oldExchange;

    public UnionProcessor() {
        super();
    }

    public UnionProcessor(Exchange newExchange, Exchange oldExchange) {
        super();
        setNewExchange(newExchange);
        setOldExchange(oldExchange);
    }

    public Exchange getNewExchange() {
        return newExchange;
    }

    public void setNewExchange(Exchange newExchange) {
        this.newExchange = newExchange;
    }

    public Exchange getOldExchange() {
        return oldExchange;
    }

    public void setOldExchange(Exchange oldExchange) {
        this.oldExchange = oldExchange;
    }

    @Override
    public Exchange doExecuteTask() {
        var newBean = getNewExchange().getIn().getBody(LlamaBean.class);
        List<LlamaBean> beans;
        if (this.oldExchange == null) {
            beans = new ArrayList<>();
            beans.add(newBean);
            this.newExchange.getIn().setBody(beans);
            return this.newExchange;
        }

        beans = ProcessorUtils.asFxkBeanList(this.oldExchange);
        beans.add(newBean);
        this.oldExchange.getIn().setBody(beans);
        return this.oldExchange;
    }

    @Override
    public String toString() {
        return "UnionProcessor{" +
                "newExchange=" + newExchange +
                ", oldExchange=" + oldExchange +
                '}';
    }
}
