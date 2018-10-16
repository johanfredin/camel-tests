package com.github.johanfredin.llama.processor.bean;

import org.apache.camel.Exchange;
import com.github.johanfredin.llama.bean.LlamaBean;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Used to alter the data in a collection based in the passed in consumer function.
 * Works with a single exchange that is expected to contain a body of a llama bean collection.
 * @param <T> any class extending {@link LlamaBean}
 */
public class TransformBeansProcessor<T extends LlamaBean> extends SimpleBeanProcessor<T> {

    private Consumer<T> function;

    /**
     * Create a new instance
     * @param exchange the exchange to modify
     * @param function the transformBeans function to apply
     */
    public TransformBeansProcessor(Exchange exchange, Consumer<T> function) {
        super(exchange);
        setFunction(function);
    }

    /**
     * @param function the transformBeans function to apply
     */
    private void setFunction(Consumer<T> function) {
        this.function = function;
    }

    /**
     * @return the transformBeans function to apply
     */
    public Consumer<T> getFunction() {
        return function;
    }

    @Override
    public List<T> processData(List<T> beans) {
        return beans
                .stream()
                .peek(this.function)
                .collect(Collectors.toList());
    }


}
