package com.github.johanfredin.llama.utils;

/**
 * Used with the {@link Endpoint} class to build source uris.
 * Each entry holds a string value that will be used in the uris.
 *
 * @author johan
 */
public enum InputOptions {

    /**
     * Specify we want <b>noop=true</b> e.g keep the file we are reading from.
     */
    KEEP("noop=true"),
    /**
     * Specify we don't want to keep the file we are reading from e.g <b>noop=false</b>
     */
    DISCARD("noop=false");

    private String option;

    InputOptions(String option) {
        this.option = option;
    }

    /**
     * @return the string value of given entry
     */
    public String getOption() {
        return option;
    }
}
