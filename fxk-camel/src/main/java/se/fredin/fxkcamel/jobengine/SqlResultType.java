package se.fredin.fxkcamel.jobengine;

public enum SqlResultType {

    ITERATE("true"),
    ALL("false");

    private String label;

    SqlResultType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
