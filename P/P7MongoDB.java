package org.vertx;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.bson.Document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class P7MongoDB extends AbstractVerticle {

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);

        client = MongoClients.create();
        database = client.getDatabase("d");
        collection = database.getCollection("student");


        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/student/:studentID").consumes("application/json").handler(routingContext -> {
            try{
                int studentID = 0;

                studentID = Integer.parseInt(routingContext.pathParam("studentID"));

                boolean isExist = false;
                for(Document doc: collection.find(new Document("id", studentID))){
                    isExist = true;
                }
                if(isExist){
                    routingContext.response().setStatusCode(400).end("ID Duplication");
                    return;
                }


                JsonObject obj = routingContext.body().asJsonObject();
                obj.put("id", studentID);

                collection.insertOne(Document.parse(obj.toString()));
                routingContext.response().setStatusCode(200).end();
            }catch (Exception e){
                routingContext.response().setStatusCode(400).end("ID Duplication");
                return;
            }
        });

        router.get("/student").handler(routingContext -> {
            try {

                JsonArray returnValues = new JsonArray();

                MongoCursor<Document> cursor = collection.find().projection(new Document("id", true)).iterator();
                while(cursor.hasNext()){
                    returnValues.add(cursor.next().getInteger("id"));
                }

                routingContext.response().setStatusCode(200).putHeader("Content-Type", "application/json").end(returnValues.toString());
            }catch(Exception e){
                routingContext.response().setStatusCode(400).end();
                return;
            }
        });
        router.get("/student/:studentID").handler(r->{
            try {
                JsonObject obj = new JsonObject();

                int studentID = Integer.parseInt(r.pathParam("studentID"));



                MongoCursor<Document> cursor = collection.find(new Document("id", studentID)).projection(new Document("_id", false)).iterator();
                Document student = null;
                while(cursor.hasNext()){
                    student = cursor.next();
                }

                if(student == null){
                    r.response().setStatusCode(400).end();
                    return;
                }
                else{
                    r.response().setStatusCode(200).putHeader("Content-Type", "applicaion/json").end(student.toJson());

                }

            }catch(Exception e){
                r.response().setStatusCode(400).end();
                return;
            }
        });

        //DELETE
        router.delete("/student/:studentID").handler(r->{
            try {
                int studentID = Integer.parseInt(r.pathParam("studentID"));

                DeleteResult dr = collection.deleteOne(new Document("id", studentID));
                if(dr.getDeletedCount() == 0){
                    r.response().setStatusCode(400).end();
                    return;
                }
                r.response().setStatusCode(200).end();
            }catch(Exception e){
                r.response().setStatusCode(400).end();
                return;
            }
        });

        server.requestHandler(router).listen(8080);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        //vertx.deployVerticle("org.vertx.P1", new DeploymentOptions().setInstances(4));
        vertx.deployVerticle(new P7MongoDB());
    }
}
