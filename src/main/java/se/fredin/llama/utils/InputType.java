package se.fredin.llama.utils;

/**
 * Used with the {@link Endpoint} class to build string uris to I/O sources.
 * Each entry contains a string value that will be used in the uris.
 *
 * @author johan
 */
public enum InputType {

    /**
     * Source is from/to a file
     */
    FILE("file"),
    /**
     * Read from/write to a database with an sql.
     */
    SQL("sql"),
    /**
     * Consume from/produce to a JPA entity class.
     */
    JPA("jpa");

    private final String type;

    InputType(String type) {
        this.type = type;
    }

    /**
     * @return the string value of given entry
     */
    public String getType() {
        return type;
    }
}
