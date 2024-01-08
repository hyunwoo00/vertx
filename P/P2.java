package org.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.HashMap;

public class P2 extends AbstractVerticle {
    private HashMap<String, Student> map = new HashMap<String, Student>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.get("/a").handler(routingContext -> {
            String k1v = routingContext.request().getParam("k1");
            String name = routingContext.request().getParam("name");
            String id = routingContext.request().getParam("id");
           routingContext.response().end("Hi there, " + name);
        });
        server.requestHandler(router).listen(8080);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        //vertx.deployVerticle("org.vertx.P1", new DeploymentOptions().setInstances(4));
        vertx.deployVerticle(new P2());
    }
}
