package se.fredin.fxkcamel.jobengine.utils;

import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import se.fredin.fxkcamel.jobengine.mock.bean.JobEngineBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JobUtils {

    public static final char AND = '&';
    public static final char ARGS = '?';
    public static final char EQUALS = '=';


    public static String file(String url, String fileName) {
        return fileSource(InputType.FILE, url, fileName, InputOptions.KEEP);
    }

    public static String file(String url, String fileName, InputOptions options) {
        return fileSource(InputType.FILE, url, fileName, options);
    }

    public static String fileSource(InputType inputType, String url, String fileName, InputOptions option) {
        return new StringBuilder()
                .append(inputType.getType()).append(':').append(url)
                .append("/")
                .append(ARGS)
                .append("fileName").append(EQUALS).append(fileName)
                .append(AND)
                .append(option.getOption())
                .toString();
    }

    public static String sql(String query) {

        return sql(query, null, true, SqlResultType.ALL, null);
    }

    public static String sql(String query, String dataSource) {
        return sql(query, null, true, SqlResultType.ALL, null);
    }

    public static String sql(String query, Class entityClass) {
        return sql(query, entityClass, true, SqlResultType.ALL, null);
    }

    public static String sql(String query, Class outputClass, boolean reuseQuery, SqlResultType resultType) {
        return sql(query, outputClass, reuseQuery, resultType, null);
    }

    public static String sql(String query, Class outputClass, boolean reuseQuery, SqlResultType resultType, String dataSource) {
        StringBuilder uriBuilder = new StringBuilder()
                .append(InputType.SQL.getType()).append(':').append(query)
                .append(ARGS)
                .append("outputClass").append(EQUALS).append(outputClass.getCanonicalName())
                .append(AND)
                .append("noop").append(EQUALS).append(Boolean.toString(reuseQuery))
                .append(AND)
                .append("useIterator").append(EQUALS).append(resultType.getLabel());

        if (dataSource != null) {
            uriBuilder.append(AND)
                    .append("datasource").append(EQUALS).append('#').append(dataSource);
        }

        return uriBuilder.toString();
    }

    public static boolean isTrueAny(boolean... conditions) {
        return isTrue(Operator.OR, conditions);
    }

    public static boolean isTrueAll(boolean... conditions) {
        return isTrue(Operator.AND, conditions);
    }


    public static boolean isTrue(Operator operator, boolean... conditions) {
        boolean trueStatementFound = false;
        for (boolean condition : conditions) {
            if (!condition) {
                if (operator == Operator.AND) {
                    return false;
                }
            }
            trueStatementFound = true;
        }

        if (operator == Operator.OR) {
            return trueStatementFound;
        }
        return true;
    }


    public static <T> List<T> asList(Exchange e) {
        return new ArrayList<T>(e.getIn().getBody(List.class));
    }

    public static <T extends JobEngineBean> Map<Object, List<T>> asMap(Exchange e) {
        return JobUtils.<T>asList(e).stream().collect(Collectors.groupingBy(T::getId));
    }

    public static String getTransformedUrl(String immUrl, String immUrlPrefix, String outputUrlPrefix) {
        String url = immUrlPrefix.toLowerCase().replace("\\", "/");
        immUrl = immUrl.toLowerCase().replace("\\", "/");
        String outUrl = outputUrlPrefix.toLowerCase().replace("\\", "/");

        return immUrl.replace(url, outUrl);
    }

    public static JacksonXMLDataFormat toXml(boolean prettyPrint) {
        JacksonXMLDataFormat dataFormat = new JacksonXMLDataFormat();
        dataFormat.setPrettyPrint(prettyPrint);
        return dataFormat;
    }


}
