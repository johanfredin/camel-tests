package se.fredin.llama.utils;

public enum InputOptions {

    KEEP("noop=true"),
    DISCARD("noop=false");

    private String option;

    InputOptions(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }
}
