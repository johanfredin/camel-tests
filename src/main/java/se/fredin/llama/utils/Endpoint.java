package se.fredin.llama.utils;

public class Endpoint {

    public static final String INPUT_DIR = "input-directory";
    public static final String OUTPUT_DIR = "output-directory";

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
        return inputType.getType() + ":" + url + "/" + ARGS + "fileName" + EQUALS + fileName + AND + option.getOption();
    }

    public static String sql(String query) {
        return sql(query, null, true, SqlResultType.ALL, null);
    }

    public static String sql(String query, String dataSource) {
        return sql(query, null, true, SqlResultType.ALL, dataSource);
    }

    public static String sql(String query, Class entityClass) {
        return sql(query, entityClass, true, SqlResultType.ALL, null);
    }

    public static String sql(String query, Class outputClass, boolean reuseQuery, SqlResultType resultType) {
        return sql(query, outputClass, reuseQuery, resultType, null);
    }

    public static String sql(String query, Class outputClass, boolean reuseQuery, SqlResultType resultType, String dataSource) {
        var uriBuilder = new StringBuilder()
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
}
