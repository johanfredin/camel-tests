package se.fredin.llama.jobengine.utils;

import se.fredin.llama.jobengine.task.Field;

import java.util.Arrays;
import java.util.List;

public class TaskUtils {

    public static List<Field> fields(Field... fields) {
        return Arrays.asList(fields);
    }

    public static Field field(String name) {
        return field(name, name);
    }

    public static Field field(String name, String outName) {
        return new Field(name, outName);
    }

}
