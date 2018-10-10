package se.fredin.llama.processor.generic;

import org.apache.camel.Exchange;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CsvTransformProcessor extends SimpleGenericProcessor {

    private Consumer<Map<String, String>> function;

    protected CsvTransformProcessor(Consumer<Map<String, String>> function, boolean includeHeader) {
        this(null, function, includeHeader);
    }

    public CsvTransformProcessor(Exchange exchange, Consumer<Map<String, String>> function) {
        this(exchange, function, false);
    }

    public Consumer<Map<String, String>> getFunction() {
        return function;
    }

    public void setFunction(Consumer<Map<String, String>> function) {
        this.function = function;
    }

    public CsvTransformProcessor(Exchange exchange, Consumer<Map<String, String>> function, boolean includeHeader) {
        super(exchange);
        this.includeHeader = includeHeader;
        this.function = function;
    }

    @Override
    public List<Map<String, String>> processData(List<Map<String, String>> records) {
        return records.
                stream()
                .peek(this.function)
                .collect(Collectors.toList());
    }

}
