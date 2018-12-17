/*
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
package com.github.johanfredin.llama;

import com.github.johanfredin.llama.utils.InputOptions;
import com.github.johanfredin.llama.utils.InputType;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.BindyType;

/**
 * Helping abstraction layer that extends the camel {@link RouteBuilder} that all
 * that is used for creating routes. Holds helper methods for retrieving spring boot
 * properties and some default methods for getting a route up and running.
 *
 * @author johan
 */
public abstract class LlamaRoute extends RouteBuilder {

    /**
     * When using several routes and they need unique sequential startup orders then this
     * property will be useful. Declared static so the current order will be persisted between
     * instances.
     */
    public static int startupOrder = 1;

    /**
     * Increments {@link #startupOrder} by one. Call this when setting startup order
     * of your routes when you have many of them and you need a simple way to ensure
     * a new unique order is given
     *
     * @return the startup order +1
     */
    public static int nextAvailableStartup() {
        return startupOrder++;
    }

    /**
     * Access a property from <b>application.properties</b> by passing in the name of the property.
     *
     * @param property the property we want the value for.
     * @return the value of the property or the name of the property surrounced by {{}} if the property is not found.
     */
    protected String prop(String property) {
        return "{{" + property + "}}";
    }

    /**
     * When we have a file that can be used with {@link BindyType} to unmarshal into a collection
     * of beans mapped up as bindy objects and then passed further to a seda endpoint
     * to be picked up later by another route. This method does just that.
     *
     * @param routeId      the id to give the route
     * @param directory    the directory of the file we want to fetch.
     * @param fileName     the name of the file we want to fetch.
     * @param clazz        the class to unmarshal the data from the file to. Needs to follow the {@link BindyType}
     *                     specification.
     * @param endpoint     the endpoint to send the the route to. Will be started with "seda:"
     * @param startupOrder the startup order of this route.
     * @return the name of the endpoint.
     */
    protected RouteDefinition getRoute(String routeId, String directory, String fileName, Class clazz, String endpoint, int startupOrder) {
        return getRoute(routeId, directory, fileName, clazz, endpoint, startupOrder, true);
    }

    /**
     * When we have a file that can be used with {@link BindyType} to unmarshal into a collection
     * of beans mapped up as bindy objects and then passed further to a seda endpoint
     * to be picked up later by another route. This method does just that.
     *
     * @param routeId      the id to give the route
     * @param directory    the directory of the file we want to fetch.
     * @param fileName     the name of the file we want to fetch.
     * @param clazz        the class to unmarshal the data from the file to. Needs to follow the {@link BindyType}
     *                     specification.
     * @param endpoint     the endpoint to send the the route to. Will be started with "seda:"
     * @param startupOrder the startup order of this route.
     * @param autoStart    whether or not the route should automatically or not (default is true)
     * @return the name of the endpoint.
     */
    protected RouteDefinition getRoute(String routeId, String directory, String fileName, Class clazz, String endpoint, int startupOrder, boolean autoStart) {
        return from(file(directory, fileName))
                .routeId(routeId)
                .autoStartup(autoStart)
                .unmarshal()
                .bindy(BindyType.Csv, clazz)
                .to("seda:" + endpoint)
                .startupOrder(startupOrder);
    }

    /**
     * Simplified way to get a route up and running. Reads from a file expected to be of .csv format.
     * Converts the content of that file to a collection of ordered maps. Sends to a seda:endpoint and gives
     * it a startup order.
     *
     * @param routeId      the unique id to give the route
     * @param directory    the directory where the csv file is expected to exits
     * @param fileName     name of the csv file to read
     * @param endpoint     name of the endpoint (seda: will be appended before the value you pass in)
     * @param startupOrder the startup order of the route
     * @return the endpoint name
     */
    protected RouteDefinition getRoute(String routeId, String directory, String fileName, String endpoint, int startupOrder) {
        return getRoute(routeId, directory, fileName, endpoint, startupOrder, true);
    }

    /**
     * Simplified way to get a route up and running. Reads from a file expected to be of .csv format.
     * Converts the content of that file to a collection of ordered maps. Sends to a seda:endpoint and gives
     * it a startup order.
     *
     * @param routeId      the unique id to give the route
     * @param directory    the directory where the csv file is expected to exits
     * @param fileName     name of the csv file to read
     * @param endpoint     name of the endpoint (seda: will be appended before the value you pass in)
     * @param startupOrder the startup order of the route
     * @param autoStart    whether or not the route should automatically or not (default is true)
     * @return the endpoint name
     */
    protected RouteDefinition getRoute(String routeId, String directory, String fileName, String endpoint, int startupOrder, boolean autoStart) {
        return from(file(directory, fileName))
                .routeId(routeId)
                .autoStartup(autoStart)
                .unmarshal(csvToCollectionOfMaps())
                .to("seda:" + endpoint)
                .startupOrder(startupOrder);
    }

    /**
     * Calls {@link #csvToCollectionOfMaps(char, boolean)} with default values <b>';', true</b>
     * Converts the content of a .csv file into a list of maps where key/values are all strings.
     * Each entry in the list will be a map where <b>key=header name, value=value at header index.</b>.
     *
     * @return the content of a .csv file as a list of ordered maps.
     */
    protected CsvDataFormat csvToCollectionOfMaps() {
        return csvToCollectionOfMaps(';', true);
    }

    /**
     * Calls {@link #csvToCollectionOfMaps(char, boolean)} with passed in delimiter and default value true for ordered maps
     * Converts the content of a .csv file into a list of maps where key/values are all strings.
     * Each entry in the list will be a map where <b>key=header name, value=value at header index.</b>.
     *
     * @param delimiter the delimiter used to separate the fields in the .csv file (default is ';')
     * @return the content of a .csv file as a list of ordered maps.
     */
    protected CsvDataFormat csvToCollectionOfMaps(char delimiter) {
        return csvToCollectionOfMaps(delimiter, true);
    }

    /**
     * Converts the content of a .csv file into a list of maps where key/values are all strings.
     * Each entry in the list will be a map where <b>key=header name, value=value at header index.</b>.
     * Here we have the option to disable ordered maps and use standard maps. This could potentially
     * be useful when we have a really big amount of data or we simply don't care if the values
     * are ordered or not.
     *
     * @param delimiter     the delimiter used to separate the fields in the .csv file (default is ';')
     * @param isOrderedMaps whether to use ordered maps or standard maps (default is true)
     * @return the content of a .csv file as a list of ordered (or standard) maps.
     */
    protected CsvDataFormat csvToCollectionOfMaps(char delimiter, boolean isOrderedMaps) {
        return csvToCollectionOfMaps(isOrderedMaps, delimiter, false, '"', true,
                null, false, "", false);
    }

    /**
     * Converts the content of a .csv file into a list of maps where key/values are all strings.
     * Each entry in the list will be a map where <b>key=header name, value=value at header index.</b>.
     * Here we have the option to disable ordered maps and use standard maps.
     * Ordered maps are required if we need to make sure the fields are given to the map and returned in the same
     * order as in the file. If we have a really big amount of data or we simply don't care if the values
     * are ordered or not then set this to false
     *
     * @param isOrderedMaps           whether to use ordered maps or standard maps
     * @param delimiter               the character used to separate the csv columns
     * @param allowMissingColumnNames whether to allow if a column is missing or not
     * @param escapeChar              character that should be interpreted as an escape character
     * @param ignoreEmptyLines        whether or not to skip an empty line in the file
     * @param quoteChar               if fields are wrapped in quotes we should specify what character is used for that. Pass in null if
     *                                no quote char is used in the file.
     * @param skipHeaderRecord        whether or not to skip the header record.
     * @param nullValue               a default value to use when we encounter null in a column.
     * @param ignoreSurroundingSpaces whether or not to skip spaces before and after a column field value.
     * @return the content of a .csv file as a list of ordered (or standard) maps.
     */
    protected CsvDataFormat csvToCollectionOfMaps(boolean isOrderedMaps, char delimiter, boolean allowMissingColumnNames,
                                                  char escapeChar, boolean ignoreEmptyLines, Character quoteChar, boolean skipHeaderRecord,
                                                  String nullValue, boolean ignoreSurroundingSpaces) {
        var format = new CsvDataFormat();
        format.setDelimiter(delimiter);
        format.setAllowMissingColumnNames(allowMissingColumnNames);
        format.setEscape(escapeChar);
        format.setIgnoreEmptyLines(ignoreEmptyLines);
        if (quoteChar != null) {
            format.setQuote(quoteChar);
        }
        format.setSkipHeaderRecord(skipHeaderRecord);
        format.setNullString(nullValue);
        format.setIgnoreSurroundingSpaces(ignoreSurroundingSpaces);
        if (isOrderedMaps) {
            format.setUseOrderedMaps(true);
        } else {
            format.setUseMaps(true);
        }
        return format;
    }

    /**
     * Create a new file uri where we don't want to move the file.
     *
     * @param url      directory of the file
     * @param fileName name of the file
     * @return a valid file uri with noop=true
     */
    protected String file(String url, String fileName) {
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
    protected String file(String url, String fileName, InputOptions options) {
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
    protected String fileSource(InputType inputType, String url, String fileName, InputOptions option) {
        return inputType.getType() + ':' + url + "/?fileName=" + fileName + '&' + option.getOption();
    }

    /**
     * Create a new sql uri. Result will be stored in a collection of JPA entities
     *
     * @param query       the sql query
     * @param outputClass a valid JPA class that can represent the query.
     * @return a valid sql uri
     */
    protected String sql(String query, Class outputClass) {
        return sql(query, outputClass, true, false, true);
    }

    /**
     * Create a new sql uri. Result will be stored in a collection of JPA entities
     *
     * @param query               the sql query
     * @param outputClass         a valid JPA class that can represent the query.
     * @param routeEmptyResultSet whether or not to continue the routing if the query result is empty, if false route will wait until data can be fetched. (default is true)
     * @return a valid sql uri
     */
    protected String sql(String query, Class outputClass, boolean routeEmptyResultSet) {
        return sql(query, outputClass, true, false, routeEmptyResultSet);
    }

    /**
     * Create a new sql uri. Result will be stored in a collection of JPA entities
     *
     * @param query               the sql query
     * @param outputClass         a valid JPA class that can represent the query.
     * @param reuseQuery          whether or not the source can be accessed again (default is true)
     * @param useIterator         if true then result will be processed one by one, if false then the entire result set is returned (default is false)
     * @param routeEmptyResultSet whether or not to continue the routing if the query result is empty, if false route will wait until data can be fetched. (default is true)
     * @return a valid sql uri
     */
    protected String sql(String query, Class outputClass, boolean reuseQuery, boolean useIterator, boolean routeEmptyResultSet) {
        return new StringBuilder()
                .append(InputType.SQL.getType()).append(':').append(query)
                .append("?outputClass=").append(outputClass.getCanonicalName())
                .append("&noop=").append(reuseQuery)
                .append("&useIterator=").append(useIterator)
                .append("&routeEmptyResultSet=").append(routeEmptyResultSet)
                .toString();
    }

    /**
     * Create a new sql uri. Result will be stored in a List of Maps
     *
     * @param query the sql query
     * @return a valid sql uri
     */
    protected String sql(String query) {
        return sql(query, true, false, true);
    }

    /**
     * Create a new sql uri. Result will be stored in a List of Maps
     *
     * @param query               the sql query
     * @param routeEmptyResultSet whether or not to continue the routing if the query result is empty, if false route will wait until data can be fetched. (default is true)
     * @return a valid sql uri
     */
    protected String sql(String query, boolean routeEmptyResultSet) {
        return sql(query, true, false, routeEmptyResultSet);
    }

    /**
     * Create a new sql uri. Result will be stored in a List of Maps
     *
     * @param query               the sql query
     * @param reuseQuery          whether or not the source can be accessed again (default is true)
     * @param useIterator         if true then result will be processed one by one, if false then the entire result set is returned (default is false)
     * @param routeEmptyResultSet whether or not to continue the routing if the query result is empty, if false route will wait until data can be fetched. (default is true)
     * @return a valid sql uri
     */
    protected String sql(String query, boolean reuseQuery, boolean useIterator, boolean routeEmptyResultSet) {
        return new StringBuilder()
                .append(InputType.SQL.getType()).append(':').append(query)
                .append("?noop=").append(reuseQuery)
                .append("&useIterator=").append(useIterator)
                .append("&routeEmptyResultSet=").append(routeEmptyResultSet)
                .toString();
    }

    /**
     * Used when we want to stop polling a route. Will make an async call to the control bus passing in
     * the id of the route we want to stop.
     * @param routeId the id of the route we want to stop
     * @return the uri string <b>controlbus:route?routeId=routeId&amp;action=stop&amp;async=true</b> where routeId=routeId param
     */
    protected String controlBus(String routeId) {
        return controlBus(routeId, "stop");
    }

    /**
     * Used when we want to call the control bus and make it do something, could be stopping a route for example
     * the id of the route we want to stop.
     * @param routeId the id of the route we want to invoke
     * @param action the action we want the control bus to take.
     * @return the uri string <b>controlbus:route?routeId=routeId&amp;action=action&amp;async=true</b> where routeId=routeId param and action=action param
     */
    protected String controlBus(String routeId, String action) {
        return "controlbus:route?routeId=" + routeId + "&action=" + action + "&async=true";
    }
}


