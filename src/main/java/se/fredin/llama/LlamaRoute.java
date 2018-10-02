package se.fredin.llama;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import se.fredin.llama.utils.Endpoint;

/**
 * Helping abstraction layer that extends the camel {@link RouteBuilder} that all
 * that is used for creating routes. Holds helper methods for retrieving spring boot
 * properties and some deault methods for getting a route up and running.
 * @author johan
 */
public abstract class LlamaRoute extends RouteBuilder {

    /**
     * Access a property from <b>application.properties</b> by passing in the name of the property.
     * @param property the property we want the value for.
     * @return the value of the property or a malformed string if the property is not found.
     */
    protected String prop(String property) {
        return "{{" + property + "}}";
    }

    /**
     * Calls {@link #prop(String)} with {@link Endpoint#INPUT_DIR} as parameter
     * @return the default input directory property (if one specified).
     */
    protected String defaultInputDir() {
        return prop(Endpoint.INPUT_DIR);
    }

    /**
     * Calls {@link #prop(String)} with {@link Endpoint#OUTPUT_DIR} as parameter
     * @return the default output directory property (if one specified).
     */
    protected String defaultOutputDir() {
        return prop(Endpoint.OUTPUT_DIR);
    }

    /**
     * Reflective call to {@link #getRoute(String, String, String, Class, String, int)}
     * passing in {@link #defaultInputDir()} as the directory parameter.
     * @param routeId the id to give the route
     * @param fileName the name of the file we want to fetch.
     * @param clazz the class to unmarshal the data from the file to. Needs to follow the {@link BindyType}
     *              specification.
     * @param endpoint the endpoint to send the the route to. Will be started with "seda:"
     * @param startupOrder the startup order of this route.
     * @return the endpoint.
     */
    protected String getRoute(String routeId, String fileName, Class clazz, String endpoint, int startupOrder) {
        return getRoute(routeId, defaultInputDir(), fileName, clazz, endpoint, startupOrder);
    }

    /**
     * When we have a file that can be used with {@link BindyType} to unmarshal into a collection
     * of {@link se.fredin.llama.bean.LlamaBean} implementations and then passed further to a seda endpoint
     * to be picked up later by another route. This method does just that.
     * @param routeId the id to give the route
     * @param directory the directory of the file we want to fetch.
     * @param fileName the name of the file we want to fetch.
     * @param clazz the class to unmarshal the data from the file to. Needs to follow the {@link BindyType}
     *              specification.
     * @param endpoint the endpoint to send the the route to. Will be started with "seda:"
     * @param startupOrder the startup order of this route.
     * @return the endpoint.
     */
    protected String getRoute(String routeId, String directory, String fileName, Class clazz, String endpoint, int startupOrder) {
        from(Endpoint.file(directory, fileName))
                .routeId(routeId)
                .unmarshal()
                .bindy(BindyType.Csv, clazz)
                .to("seda:" + endpoint)
                .startupOrder(startupOrder);

        return "seda:" + endpoint;
    }
}


