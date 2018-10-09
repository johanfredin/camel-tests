package se.fredin.llama.processor.bean;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.SimpleProcessor;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterBeansProcessor<T extends LlamaBean> extends SimpleProcessor<T> {

    private Predicate<T> function;

    public FilterBeansProcessor(Exchange exchange, Predicate<T> function) {
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
