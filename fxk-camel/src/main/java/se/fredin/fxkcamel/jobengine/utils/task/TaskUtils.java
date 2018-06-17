package se.fredin.fxkcamel.jobengine.utils.task;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.bean.JobEngineBean;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;
import se.fredin.fxkcamel.jobengine.utils.task.join.OutEntity;
import se.fredin.fxkcamel.jobengine.utils.task.join.RecordSelection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskUtils {

    public static <T> Exchange union(Exchange oldExchange, Exchange newExchange, Class<T> beanClass) {
        T newBean = newExchange.getIn().getBody(beanClass);
        List<T> beans = null;
        if (oldExchange == null) {
            beans = new ArrayList<>();
            beans.add(newBean);
            newExchange.getIn().setBody(beans);
            return newExchange;
        }

        beans = JobUtils.<T>asList(oldExchange);
        beans.add(newBean);
        oldExchange.getIn().setBody(beans);
        return oldExchange;
    }



}
