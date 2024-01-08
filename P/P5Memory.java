package org.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;

public class P5Memory extends AbstractVerticle {

    private HashMap<Integer, Student> data = new HashMap<Integer, Student>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/student/:studentID").consumes("application/json").handler(routingContext -> {
            int studentID = 0;
            try {
                studentID = Integer.parseInt(routingContext.pathParam("studentID"));
            }catch(Exception e){

            }
            if(data.containsKey(studentID)){ // not success return 400
                routingContext.response().setStatusCode(400).end("ID Duplication");
                return;
            }

            JsonObject obj = routingContext.body().asJsonObject();
            String name = obj.getString("name");
            Double a = obj.getDouble("a");
            Double b = obj.getDouble("b");
            Double c = obj.getDouble("c");
            Student student = new Student(studentID, name, a, b, c);
            data.put(studentID, student);
            routingContext.response().setStatusCode(200).end();

        });
        router.get("/student").handler(routingContext -> {
            JsonArray returnValues = new JsonArray();
            for(Integer id: data.keySet()){
                returnValues.add(id);
            }

           routingContext.response().putHeader("Content-Type", "application/json").end(returnValues.toString());
        });
        router.get("/student/:studentID").handler(r->{
           JsonObject obj = new JsonObject();

           int studentID = Integer.parseInt(r.pathParam("studentID"));
            if(!data.containsKey(studentID)){
                r.response().setStatusCode(400).end();
                return;
            }
           Student student = data.get(studentID);

           obj.put("id", studentID);
           obj.put("name", student.getName());
           obj.put("attendance", student.getA());
           obj.put("asssignment", student.getB());
           obj.put("exam", student.getC());

           r.response().putHeader("Content-Type", "applicaion/json").end(obj.toString());
        });

        //DELETE
        router.delete("/student/:studentID").handler(r->{
           int studentID = Integer.parseInt(r.pathParam("studentID"));
           if(!data.containsKey(studentID)){ // key 포함 x
               r.response().setStatusCode(400).end("not exist");
               return;
           }
           data.remove(studentID);
           r.response().setStatusCode(200).end();
        });

        server.requestHandler(router).listen(8080);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        //vertx.deployVerticle("org.vertx.P1", new DeploymentOptions().setInstances(4));
        vertx.deployVerticle(new P5Memory());
    }
}
