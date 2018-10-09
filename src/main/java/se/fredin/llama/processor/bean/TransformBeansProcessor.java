package se.fredin.llama.processor.bean;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.SimpleProcessor;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TransformBeansProcessor<T extends LlamaBean> extends SimpleProcessor<T> {

    private Consumer<T> function;

    public TransformBeansProcessor(Exchange exchange, Consumer<T> function) {
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
