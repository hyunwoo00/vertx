package org.vertx;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.bson.Document;


//database로부터 값을 가져오는 Verticle
public class ServiceVerticle extends AbstractVerticle {

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    @Override
    public void start() throws Exception {
        super.start();

        client = MongoClients.create();
        database = client.getDatabase("web");
        collection = database.getCollection("user");


    }
}
