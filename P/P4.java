package org.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class P4 extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/student/:studentID").handler(routingContext -> {
            String studentID = routingContext.pathParam("studentID");
            String body = routingContext.body().asString();
            JsonObject obj = new JsonObject(body);
            String name = obj.getString("name");
            routingContext.response().end("Hi, " + name + " " +studentID);
        });
        
        server.requestHandler(router).listen(8080);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        //vertx.deployVerticle("org.vertx.P1", new DeploymentOptions().setInstances(4));
        vertx.deployVerticle(new P4());
    }
}
