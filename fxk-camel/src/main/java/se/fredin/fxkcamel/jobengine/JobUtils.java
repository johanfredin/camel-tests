package se.fredin.fxkcamel.jobengine;

import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;

import java.util.List;
import java.util.ArrayList;

public class JobUtils {

    public static String file(String url, String fileName) {
        return getSource(InputType.FILE, url, fileName, InputOptions.KEEP);
    }

    public static String file(String url, String fileName, InputOptions options) {
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

    public static<T> List<T> asList(Exchange e) {
        return new ArrayList<T>(e.getIn().getBody(List.class));
    }

    public static JacksonXMLDataFormat toXml(boolean prettyPrint) {
        JacksonXMLDataFormat dataFormat = new JacksonXMLDataFormat();
        dataFormat.setPrettyPrint(prettyPrint);
        return dataFormat;
    }


}
