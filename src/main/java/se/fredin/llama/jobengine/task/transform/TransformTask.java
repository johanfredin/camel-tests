package se.fredin.llama.jobengine.task.transform;

import org.apache.camel.Exchange;
import se.fredin.llama.jobengine.bean.FxKBean;
import se.fredin.llama.jobengine.task.SimpleTask;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TransformTask<T extends FxKBean> extends SimpleTask<T> {

    private Consumer<T> function;

    public TransformTask(Exchange exchange, Consumer<T> function) {
        super(exchange);
        setFunction(function);
    }

    private void setFunction(Consumer<T> function) {
        this.function = function;
    }

    public Consumer<T> getFunction() {
        return function;
    }

    @Override
    public List<T> transformData(List<T> beans) {
        return beans
                .stream()
                .peek(this.function)
                .collect(Collectors.toList());
    }
}
