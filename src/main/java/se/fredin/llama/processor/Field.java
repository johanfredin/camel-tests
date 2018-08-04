package se.fredin.llama.processor;

public class Field {

    private String name;
    private String outName;

    public Field(String name) {
        this(name, name);
    }

    public Field(String name, String outName) {
        setName(name);
        setOutName(outName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutName() {
        return outName;
    }

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
