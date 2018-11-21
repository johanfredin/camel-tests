/**
 * Copyright 2018 Johan Fredin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.johanfredin.llama.utils;

/**
 * Builds URI strings that can be used with the {@link org.apache.camel.model.RouteDefinition}.
 * Instead of having to write <b>from("file:home/user/somedir?someFile.file&amp;noop=true</b>
 * We could call <b>from(Endpoint.file("home/user/somedir", "someFile.file"))</b>.
 * There are also methods for building sql uris.
 *
 * @author johan
 */
public class Endpoint {

    /**
     * Create a new file uri where we don't want to move the file.
     *
     * @param url      directory of the file
     * @param fileName name of the file
     * @return a valid file uri with noop=true
     */
    public static String file(String url, String fileName) {
        return fileSource(InputType.FILE, url, fileName, InputOptions.KEEP);
    }

    /**
     * Create a new file uri
     *
     * @param url      directory of the file
     * @param fileName name of the file
     * @param options  whether to keep the file or not (default is KEEP)
     * @return a valid file uri
     */
    public static String file(String url, String fileName, InputOptions options) {
        return fileSource(InputType.FILE, url, fileName, options);
    }

    /**
     * Create a new source uri.
     *
     * @param inputType what type of source to read from
     * @param url       directory to the source
     * @param fileName  name of the source
     * @param option    whether to keep the source or not
     * @return a valid source uri.
     */
    public static String fileSource(InputType inputType, String url, String fileName, InputOptions option) {
        return inputType.getType() + ':' + url + "/?fileName=" + fileName + '&' + option.getOption();
    }

    /**
     * Create a new sql uri.
     *
     * @param query       the sql query
     * @param outputClass a valid JPA class that can represent the query.
     * @return a valid sql uri
     */
    public static String sql(String query, Class outputClass) {
        return sql(query, outputClass, true, SqlResultType.ALL, null);
    }

    /**
     * Create a new sql uri.
     *
     * @param query       the sql query
     * @param outputClass a valid JPA class that can represent the query.
     * @param reuseQuery  whether or not the source can be accessed again (default is true)
     * @param resultType  whether to return one result per query or all in one (default is {@link SqlResultType#ALL}
     * @return a valid sql uri
     */
    public static String sql(String query, Class outputClass, boolean reuseQuery, SqlResultType resultType) {
        return sql(query, outputClass, reuseQuery, resultType, null);
    }

    /**
     * Create a new sql uri.
     *
     * @param query       the sql query
     * @param outputClass a valid JPA class that can represent the query.
     * @param reuseQuery  whether or not the source can be accessed again (default is true)
     * @param resultType  whether to return one result per query or all in one (default is {@link SqlResultType#ALL}
     * @param dataSource  name of the datasource (default is null)
     * @return a valid sql uri
     */
    public static String sql(String query, Class outputClass, boolean reuseQuery, SqlResultType resultType, String dataSource) {
        var uriBuilder = new StringBuilder()
                .append(InputType.SQL.getType()).append(':').append(query)
                .append("?outputClass=").append(outputClass.getCanonicalName())
                .append("&noop=").append(Boolean.toString(reuseQuery))
                .append("&useIterator=").append(resultType.getLabel());

        if (dataSource != null) {
            uriBuilder.append("&datasource=#").append(dataSource);
        }

        return uriBuilder.toString();
    }
}
