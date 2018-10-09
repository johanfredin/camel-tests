package se.fredin.llama.bean;

import java.io.Serializable;

/**
 * Interface that works with all the bean processors. Holds one method {@link #getId()}
 * of type {@link Serializable}. This so that it could also be persisted with JPA/Hibernate etc...
 * {@link #getId()} method is used by all the processor classes under the /bean package that operates
 * on collections/maps of llama beans to identify the records.
 *
 * @author johan fredin
 */
public interface LlamaBean extends Serializable {

    /**
     * The property (or concatenation of properties) that should be used
     * as the identifier of this instance.
     * @return the field qualifying as the identifier of the instance.
     */
    Serializable getId();

}
