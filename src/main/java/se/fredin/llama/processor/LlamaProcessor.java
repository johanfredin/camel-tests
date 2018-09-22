package se.fredin.llama.processor;

import org.apache.camel.Exchange;

/**
 * Helper interface for all custom llama processors.
 */
public interface LlamaProcessor {

    /**
     * Should be called before all processing begins.
     * Preferably post construct.
     */
    void postCreate();

    /**
     * Useful as a summary of what has happened.
     * Call this after processing.
     */
    void postExecute();

    /**
     * Main method of the processor. All logic should take place in here.
     * Camel requires that processors returns an exchange to pass to the next
     * step in the route.
     * @return the modified exchange
     */
    Exchange doExecuteProcess();

    /**
     * @return the name of this processor. Useful for logging.
     */
    String getProcessorName();

    /**
     * If we want the result type to be of another type then we should specify it here.
     * Typically the exchange body contains some sort of collection or a map. If we want
     * any of those we should specify it. Default behaviour is to return the content of the
     * exchange body the same type as it was passed in.
     * @return the type of the exchange body.
     */
    default ResultType getResultType() {
        return ResultType.AS_IS;
    }
}
