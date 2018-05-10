package se.fredin.fxkcamel.jobengine;

public class JobUtils {

    public static  String getInputFile(String url, String fileName) {
        return getSource(InputType.FILE, url, fileName, InputOptions.KEEP);
    }

    public static String getInputFile(String url, String fileName, InputOptions options) {
        return getSource(InputType.FILE, url, fileName, options);
    }

    public static String getSource(InputType inputType, String url, String fileName, InputOptions option) {
        return new StringBuilder()
                .append(inputType.getType())
                .append(url)
                .append("/?fileName=")
                .append(fileName)
                .append(option.getOption())
                .toString();
    }
}
