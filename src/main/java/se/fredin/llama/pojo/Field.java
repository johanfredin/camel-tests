package se.fredin.llama.pojo;

import se.fredin.llama.processor.LlamaProcessor;

/**
 * Used with different {@link LlamaProcessor} implementations.
 * Typically existing in collections in first(header) entry or as map keys
 * in maps. Holds a name that equals the name the field has when coming in
 * and an optional out name that the user can give it. Both {@link #name}
 * and {@link #outName} are always instantiated. When not specifying an out name
 * it will have the same value as the in name.
 * @author johan
 */
public class Field {

    private String name;
    private String outName;

    /**
     * Create a new instance. In this case the {@link #getName()}
     * and {@link #getOutName()} will get the same value.
     * @param name the name of the field. The out name will get the same value.
     */
    public Field(String name) {
        this(name, name);
    }

    /**
     * Create a new instance.
     * @param name the in name.
     * @param outName the name we want when the field is written back.
     */
    public Field(String name, String outName) {
        setName(name);
        setOutName(outName);
    }

    /**
     * @return the in name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the in name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the out name (default value={@link #getName()})
     */
    public String getOutName() {
        return outName;
    }

    /**
     * @param outName the out name.
     */
    public void setOutName(String outName) {
        this.outName = outName;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", outName='" + outName + '\'' +
                '}';
    }
}
