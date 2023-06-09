package com.uniovi.sdi2223entrega2test32;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.bson.Document;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoDB {
    Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKeySpec;
    private MongoClient mongoClient;
    private MongoDatabase mongodb;
    private String secretKey = "abcdefg";

    public MongoDB() throws NoSuchAlgorithmException, InvalidKeyException {
        secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoDatabase getMongodb() {
        return mongodb;
    }

    public void setMongodb(MongoDatabase mongodb) {
        this.mongodb = mongodb;
    }

    public void resetMongo() {
        try {
            setMongoClient(new MongoClient(new MongoClientURI(
                    "mongodb://localhost:27017")));
            setMongodb(getMongoClient().getDatabase("UrWalletPop"));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        deleteData();
        insertUsers();
        insertOffers();
        insertMessages();
    }

    private void deleteData() {
        getMongodb().getCollection("offers").drop();
        getMongodb().getCollection("logs").drop();
        getMongodb().getCollection("users").drop();
        getMongodb().getCollection("conversations").drop();
        getMongodb().getCollection("messages").drop();
    }

    private String getHash(String password) {
        byte[] hashBytes = hmacSHA256.doFinal(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void insertUsers() {
        MongoCollection<Document> usuarios = getMongodb().getCollection("users");

        Document admin = new Document().append("name", "admin").append("lastName", "a")
                .append("email", "admin@email.com")
                .append("password", getHash("admin"))
                .append("date", "11/11/2002")
                .append("amount", 100).append("role", "admin");
        usuarios.insertOne(admin);
        for (int i = 1; i < 16; i++) {
            String name = "user" + (i < 10 ? "0" : "") + i;
            usuarios.insertOne(new Document().append("name", name).append("lastName", "a")
                    .append("email", name + "@email.com")
                    .append("password", getHash(name))
                    .append("date", "11/11/2002")
                    .append("amount", i == 3 ? 0 : 100).append("role", "standard"));

        }
    }

    private void insertConversationAndMessage(Document offer,String interestedEmail,String objectIdVal){
        MongoCollection<Document> conversations = getMongodb().getCollection("conversations");
        ObjectId objectId = new ObjectId(objectIdVal);
        Document conversation = new Document()
                .append("_id",objectId)
                .append("offer", offer)
                .append("interested", interestedEmail);
        conversations.insertOne(conversation);

        MongoCollection<Document> messages = getMongodb().getCollection("messages");
        Document message = new Document()
                .append("offer", new ObjectId("000000000000000000000005"))
                .append("owner", interestedEmail)
                .append("interested", interestedEmail)
                .append("read",false)
                .append("text","Me interesa la oferta")
                .append("date", new Date());
        messages.insertOne(message);
    }

    private void insertOffers() {
        MongoCollection<Document> offers = getMongodb().getCollection("offers");
        Document offer1 = new Document().append("title", "117").append("author", "user02@email.com")
                .append("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .append("price", 5)
                .append("date", "11/11/2002")
                .append("purchase", false)
                .append("buyer", null)
                .append("feature", false);
        offers.insertOne(offer1);
        ObjectId objectId = new ObjectId("000000000000000000000005");
        Document offer2 = new Document().append("title", "118").append("author", "user02@email.com")
                .append("_id",objectId)
                .append("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .append("price", 100)
                .append("date", "11/11/2002")
                .append("purchase", false)
                .append("buyer", null)
                .append("feature", false);
        offers.insertOne(offer2);
        insertConversationAndMessage(offer2,"user03@email.com","000000000000000000000003");
        insertConversationAndMessage(offer2,"user01@email.com","000000000000000000000002");
        ObjectId objectId2 = new ObjectId("000000000000000000000001");
        Document offer3 = new Document().append("title", "119").append("author", "user02@email.com")
                .append("_id",objectId2)
                .append("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .append("price", 150)
                .append("date", "11/11/2002")
                .append("purchase", false)
                .append("buyer", null)
                .append("feature", false);
        offers.insertOne(offer3);
        for (int i = 1; i < 16; i++) {
            String name = "user" + (i < 10 ? "0" : "") + i + "@email.com";
            for (int j = 0; j < 10; j++) {
                offers.insertOne(new Document().append("title", (j <= 4 ? "Oferta " : "OFERTA ") + i + "" + j)
                        .append("author", name)
                        .append("description", "Oferta nueva")
                        .append("price", 50)
                        .append("date", "11/11/2002")
                        .append("purchase", j == 4 ? true : false)
                        .append("buyer", null)
                        .append("feature", false));
            }

        }
    }

    public long getTotalOffersCount() {
        return getMongodb().getCollection("offers").count();
    }

    public long getUsersOffersCount(String author) {
        return getMongodb().getCollection("offers").count(new Document("author", author));
    }

    public long getOffersByTitleCount(String title) {
        return getMongodb().getCollection("offers").count(new Document("title", new Document("$regex", title).append("$options", "i")));
    }

    public long getUsers() {
        return getMongodb().getCollection("users").count();
    }

    private void insertMessages(){
        MongoCollection<Document> offers = getMongodb().getCollection("messages");
        Document offer1 = new Document()
                .append("owner", "user07@email.com")
                .append("interested", "user05@email.com")
                .append("offer", new ObjectId("645692d93a07e85fc87fefa6"))
                .append("date" , new Date())
                .append("text","Hola")
                .append("read",false);
        offers.insertOne(offer1);
    }



    public Document getMessage(String owner, String interested, String offer) {
        MongoCollection<Document> messages = getMongodb().getCollection("messages");
        BasicDBObject andQuery = new BasicDBObject();

        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        obj.add(new BasicDBObject("owner", owner));
        obj.add(new BasicDBObject("interested", interested));
        obj.add(new BasicDBObject("offer", new ObjectId(offer)));
        andQuery.put("$and", obj);

        System.out.println(andQuery.toString());

        Document result = messages.find(andQuery).first();

        return result;

    }

    public Document getMessageFromUser(String owner, String interested) {
        MongoCollection<Document> messages = getMongodb().getCollection("messages");
        BasicDBObject andQuery = new BasicDBObject();

        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        obj.add(new BasicDBObject("owner", owner));
        obj.add(new BasicDBObject("interested", interested));
        andQuery.put("$and", obj);

        System.out.println(andQuery.toString());

        FindIterable<Document> findIterable = messages.find(andQuery);
        MongoCursor<Document> cursor = findIterable.iterator();
        while(cursor.hasNext()) {
            Document document = cursor.next();
            if(document != null && document.getBoolean("read") == true){
                return document;
            }
        }
        return null;
    }

}
