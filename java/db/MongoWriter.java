package db;

import com.mongodb.client.*;
import org.bson.Document;

import java.util.Map;

public class MongoWriter {
    private final MongoCollection<Document> collection;

    public MongoWriter(String uri, String db, String col) {
        MongoClient mongoClient = MongoClients.create(uri);
        this.collection = mongoClient.getDatabase(db).getCollection(col);
    }

    public void write(Map<String, Object> data) {
        collection.insertOne(new Document(data));
    }
}
