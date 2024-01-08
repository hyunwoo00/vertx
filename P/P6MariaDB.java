package org.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class P6MariaDB extends AbstractVerticle {

    private Connection connection;
    private Statement stmt;
    private HashMap<Integer, Student> data = new HashMap<Integer, Student>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);

        connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306","root","1234");
        stmt = connection.createStatement();

        stmt.executeUpdate("Create Database IF NOT EXISTS d;");
        stmt.executeUpdate("USE d;");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS student(id INT, name VARCHAR(50), a DOUBLE, b DOUBLE, c DOUBLE);");



        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/student/:studentID").consumes("application/json").handler(routingContext -> {
            try{
                int studentID = 0;

                studentID = Integer.parseInt(routingContext.pathParam("studentID"));

                ResultSet rs = stmt.executeQuery("SELECT id FROM student WHERE id = " + studentID);
                if (rs.next()) { // not success return 400
                    routingContext.response().setStatusCode(400).end("ID Duplication");
                    return;
                }

                JsonObject obj = routingContext.body().asJsonObject();
                String name = obj.getString("name");
                Double a = obj.getDouble("a");
                Double b = obj.getDouble("b");
                Double c = obj.getDouble("c");

                stmt.executeUpdate("INSERT INTO student VALUES(" + studentID +",'" + name +"',"+a+","+b+","+c+");");
                routingContext.response().setStatusCode(200).end();
            }catch (Exception e){
                routingContext.response().setStatusCode(400).end("ID Duplication");
                return;
            }
        });

        router.get("/student").handler(routingContext -> {
            try {
                ResultSet rs = stmt.executeQuery("SELECT id FROM student");
                JsonArray returnValues = new JsonArray();
                while(rs.next()){
                    returnValues.add(rs.getInt("id"));
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
                ResultSet rs = stmt.executeQuery("SELECT * FROM student WHERE id = " + studentID);

                if(!rs.next()){
                    r.response().setStatusCode(400).end();
                    return;
                }


                obj.put("id", studentID);
                obj.put("name", rs.getString("name"));
                obj.put("attendance", rs.getDouble("a"));
                obj.put("asssignment", rs.getDouble("b"));
                obj.put("exam", rs.getDouble("c"));

                r.response().setStatusCode(200).putHeader("Content-Type", "applicaion/json").end(obj.toString());
            }catch(Exception e){
                r.response().setStatusCode(400).end();
                return;
            }
        });

        //DELETE
        router.delete("/student/:studentID").handler(r->{
            try {
                int studentID = Integer.parseInt(r.pathParam("studentID"));
                ResultSet rs = stmt.executeQuery("SELECT id FROM student WHERE id = " + studentID);
                if(!rs.next()){
                    r.response().setStatusCode(400).end();
                    return;
                }
                stmt.executeUpdate("DELETE FROM student WHERE id = " + studentID);
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
        vertx.deployVerticle(new P6MariaDB());
    }
}
