package se.fredin.fxkcamel.jobengine.utils;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.bean.Item;

import java.util.ArrayList;
import java.util.List;

public class TaskUtils {

    public static <T> Exchange merge(Exchange oldExchange, Exchange newExchange, Class<T> beanClass) {
        T newBean = newExchange.getIn().getBody(beanClass);
        List<T> beans = null;
        if(oldExchange == null) {
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
