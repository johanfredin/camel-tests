package se.fredin.fxkcamel.jobengine.task.union;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.bean.FxKBean;
import se.fredin.fxkcamel.jobengine.task.BaseTask;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.ArrayList;
import java.util.List;

public class UnionTask extends BaseTask {

    private Exchange newExchange;
    private Exchange oldExchange;

    public UnionTask() {}

    public UnionTask(Exchange newExchange, Exchange oldExchange) {
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
        FxKBean newBean = getNewExchange().getIn().getBody(FxKBean.class);
        List<FxKBean> beans;
        if (this.oldExchange == null) {
            beans = new ArrayList<>();
            beans.add(newBean);
            this.newExchange.getIn().setBody(beans);
            return this.newExchange;
        }

        beans = JobUtils.asList(this.oldExchange);
        beans.add(newBean);
        this.oldExchange.getIn().setBody(beans);
        return this.oldExchange;
    }

    @Override
    public String toString() {
        return "UnionTask{" +
                "newExchange=" + newExchange +
                ", oldExchange=" + oldExchange +
                '}';
    }
}
