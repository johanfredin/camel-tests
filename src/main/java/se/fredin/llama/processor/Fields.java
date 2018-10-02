package se.fredin.llama.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holds a collection of {@link Field} instances and helper methods for manipulating that
 * collection. Instances can be created either by one of the constructors of statically
 * with predefined properties.
 * @author johan
 */
public class Fields {

    /**
     * Creates a new Fields instance with {@link #isAllFields}=<code>true</code>
     * Used for example by the {@link se.fredin.llama.processor.join.JoinCollectionsProcessor}
     * when we want ALL fields from one exchange and we don't need alternate out names.
     */
    public static final Fields ALL = new Fields(true);

    /**
     * Creates a new Fields instance with {@link #isAllFields}=<code>false</code>
     * Used for example by the {@link se.fredin.llama.processor.join.JoinCollectionsProcessor}
     * when we don't want any fields from one exchange.
     */
    public static final Fields NONE = new Fields(false);

    private boolean isAllFields;
    private List<Field> fields;

    /**
     * Create a new instance
     * @param isAllFields whether or not we want all the fields in the exchange.
     */
    public Fields(boolean isAllFields) {
        setAllFields(isAllFields);
    }

    /**
     * Create a new instance with an arbitrary amount of Field instances.
     * The params will be used to build the {@link #getFields()} collection.
     * If no fields are passed in then the {@link #getFields()} collection
     * will be instantiated with 0 values.
     * @param fields the fields to add to the {@link #getFields()} collection.
     */
    public Fields(Field... fields) {
        this(new ArrayList<>(List.of(fields)));
    }

    /**
     * Create a new instance
     * @param fields the collection of fields to use.
     */
    public Fields(List<Field> fields) {
        setFields(fields);
    }

    /**
     * Create a fields object based on passed in parameters.
     * A new field will be created for each key.
     * {@link Field#getName()} and {@link Field#getOutName()} will be the same here.
     * @param fieldNames the fields to create a fields objects of
     * @return a fields object based on passed in parameters
     */
    public static Fields of(String... fieldNames) {
        var fields = Arrays
                .stream(fieldNames)
                .map(Field::new)
                .collect(Collectors.toCollection(ArrayList::new));
        return new Fields(fields);
    }

    /**
     * Create fields object based on passed in map.
     * fields objects will be created for each map entry where map key={@link Field#getName()} and value={@link Field#getOutName()}
     *
     * @param fieldNamesAndOutputNames the key-value pairs to create field objects of.
     * @return a fields object based on passed in map.
     */
    public static Fields of(Map<String, String> fieldNamesAndOutputNames) {
        var fields = fieldNamesAndOutputNames.entrySet()
                .stream()
                .map(e -> new Field(e.getKey(), e.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));
        return new Fields(fields);
    }

    /**
     * @return whether or not to use all fields in the exchange.
     */
    public boolean isAllFields() {
        return isAllFields;
    }

    /**
     * @param allFields whether or not to use all the fields in the exchange.
     */
    public void setAllFields(boolean allFields) {
        isAllFields = allFields;
    }

    /**
     * @return the collection of fields to use.
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @param fields the collection of fields to use.
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * Add a new field to the {@link #getFields()} collection
     * if it does not already exist in it. collection is instantiated
     * if <code>null</code>
     * @param field the field to add to the {@link #getFields()} collection.
     */
    public void addField(Field field) {
        if(getFields() == null) {
            setFields(new ArrayList<>());
        }

        if(!getFields().contains(field)) {
            getFields().add(field);
        }
    }

    /**
     * @return true if {@link #getFields()} != {@code null} and not isEmpty
     */
    public boolean hasFields() {
        return getFields() != null && !getFields().isEmpty();
    }

    @Override
    public String toString() {
        return "Fields{" +
                "isAllFields=" + isAllFields +
                ", fields=" + fields +
                '}';
    }
}
