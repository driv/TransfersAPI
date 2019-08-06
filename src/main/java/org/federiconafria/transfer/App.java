package org.federiconafria.transfer;

import org.federiconafria.transfer.rest.AppConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class App {
    public static void main(String[] args) throws IOException {
        AppConfig config = new AppConfig();

        String baseUri = "http://localhost:8080/api/";
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), config);
        server.start();
    }
}
