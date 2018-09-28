package se.fredin.llama.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fields {

    public static final Fields ALL = new Fields(true);
    public static final Fields NONE = new Fields(false);

    private boolean isAllFields = false;
    private List<Field> fields;

    public Fields(boolean isAllFields) {
        setAllFields(isAllFields);
    }

    public Fields(Field... fields) {
        setFields(Arrays.asList(fields));
    }

    public Fields(List<Field> fields) {
        setFields(fields);
    }

    public boolean isAllFields() {
        return isAllFields;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setAllFields(boolean allFields) {
        isAllFields = allFields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public void addField(Field field) {
        if(getFields() == null) {
            setFields(new ArrayList<>());
        }

        if(!getFields().contains(field)) {
            getFields().add(field);
        }
    }

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
