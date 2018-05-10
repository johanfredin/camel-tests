package se.fredin.fxkcamel.jobengine;

public enum InputOptions {

    KEEP("?noop=true"),
    DISCARD("?noop=false");

    private String option;

    private InputOptions(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }
}
