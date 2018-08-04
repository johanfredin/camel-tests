package se.fredin.llama.utils;

public enum InputType {

    FILE("file"),
    SQL("sql"),
    JPA("jpa");

    private final String type;

    InputType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
