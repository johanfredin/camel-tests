package se.fredin.llama.processor.union;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.BaseProcessor;
import se.fredin.llama.utils.LlamaUtils;

import java.util.ArrayList;
import java.util.List;

public class UnionProcessor extends BaseProcessor {

    private Exchange mergingExchange;
    private Exchange mainExchange;
    private boolean isMainExchangeToReturn = true;

    public UnionProcessor() {}

    public UnionProcessor(Exchange mergingExchange, Exchange mainExchange) {
        setMergingExchange(mergingExchange);
        setMainExchange(mainExchange);
    }

    public Exchange getMergingExchange() {
        return mergingExchange;
    }

    public void setMergingExchange(Exchange mergingExchange) {
        this.mergingExchange = mergingExchange;
    }

    public Exchange getMainExchange() {
        return mainExchange;
    }

    public void setMainExchange(Exchange mainExchange) {
        this.mainExchange = mainExchange;
    }

    public boolean isMainExchangeToReturn() {
        return isMainExchangeToReturn;
    }

    public void setMainExchangeToReturn(boolean mainExhangeToReturn) {
        isMainExchangeToReturn = mainExhangeToReturn;
    }

    @Override
    protected void process() {
        var newBean = getMergingExchange().getIn().getBody(LlamaBean.class);
        List<LlamaBean> beans;
        if (this.mainExchange == null) {
            beans = new ArrayList<>();

            if(newBean != null) {
                beans.add(newBean);
            }

            this.mergingExchange.getIn().setBody(beans);
            setMainExchangeToReturn(false);
        }

        beans = LlamaUtils.asLlamaBeanList(this.mainExchange);
        if(newBean != null) {
            beans.add(newBean);
        }
        super.incProcessedRecords();
        this.mainExchange.getIn().setBody(beans);
    }

    @Override
    protected Exchange result() {
        return isMainExchangeToReturn ? this.mainExchange : this.mergingExchange;
    }

    @Override
    public String toString() {
        return "UnionProcessor{" +
                "mergingExchange=" + mergingExchange +
                ", mainExchange=" + mainExchange +
                '}';
    }
}
