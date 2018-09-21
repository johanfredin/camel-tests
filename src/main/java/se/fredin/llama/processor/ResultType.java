package se.fredin.llama.processor;

/**
 * Used with {@link LlamaProcessor} instances to determine the result type.
 * Typically result type would be some sort of collection but may also be map
 * or simply the same type as you sent in.
 */
public enum ResultType {

    /**
     * Result should be returned as a {@link java.util.List}
     */
    LIST,
    /**
     * Result should be returned as a {@link java.util.Map}
     */
    MAP,
    /**
     * Result should be returned as the same type it had coming in.
     */
    AS_IS

}
