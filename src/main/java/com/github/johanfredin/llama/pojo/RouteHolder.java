package com.github.johanfredin.llama.pojo;

import org.apache.camel.model.RouteDefinition;

/**
 * Convenient way to store a route definition, the id and the output uri of that route.
 * Getting the output uri of a route can be quite tricky.
 *
 */
public class RouteHolder {

    private final String routeId;
    private final String endpointUri;
    private final RouteDefinition routeDefinition;

    /**
     * Create a new instance
     * @param routeId the id of the route
     * @param endpointUri the endpoint uri of the route
     * @param routeDefinition the route
     */
    public RouteHolder(String routeId, String endpointUri, RouteDefinition routeDefinition) {
        this.routeId = routeId;
        this.endpointUri = endpointUri;
        this.routeDefinition = routeDefinition;
    }

    /**
     * @return the id of the route
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * @return the endpoint uri
     */
    public String getEndpointUri() {
        return endpointUri;
    }

    /**
     * @return the route
     */
    public RouteDefinition getRouteDefinition() {
        return routeDefinition;
    }

    @Override
    public String toString() {
        return "RouteHolder{" +
                "routeId='" + routeId + '\'' +
                ", endpointUri='" + endpointUri + '\'' +
                ", routeDefinition=" + routeDefinition +
                '}';
    }
}
