package se.fredin.fxkcamel.jobengine;

public enum InputType {

    FILE("file:"),
    DB("sql:"),
    JPA("jpa:");

    private final String type;

    private InputType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
