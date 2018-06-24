package se.fredin.fxkcamel.jobengine.task.filter;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.bean.FxKBean;
import se.fredin.fxkcamel.jobengine.task.BaseTask;
import se.fredin.fxkcamel.jobengine.task.SimpleTask;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterTask<T extends FxKBean> extends SimpleTask<T> {

    private Predicate<T> function;

    public FilterTask(Exchange exchange, Predicate<T> function) {
        super(exchange);
        setFunction(function);
    }

    private void setFunction(Predicate<T> function) {
        this.function = function;
    }

    public Predicate<T> getFunction() {
        return function;
    }

    @Override
    public List<T> transformData(List<T> beans) {
        return beans
                .stream()
                .filter(this.function)
                .collect(Collectors.toList());

    }
}
