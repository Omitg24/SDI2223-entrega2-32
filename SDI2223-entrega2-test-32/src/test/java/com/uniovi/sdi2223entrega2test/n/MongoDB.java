package com.uniovi.sdi2223entrega2test.n;

import com.mongodb.client.FindIterable;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
	private MongoClient mongoClient;
	private MongoDatabase mongodb;

	public MongoClient getMongoClient() {
		return mongoClient;
	}

	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public void setMongodb(MongoDatabase mongodb) {
		this.mongodb = mongodb;
	}

	public MongoDatabase getMongodb() {
		return mongodb;
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
	}

	private void deleteData() {
		getMongodb().getCollection("offers").drop();
		getMongodb().getCollection("users").drop();
		getMongodb().getCollection("conversations").drop();
		getMongodb().getCollection("messages").drop();
	}

	private void insertUsers() {
		MongoCollection<Document> usuarios = getMongodb().getCollection("users");
		Document user1 = new Document().append("name", "prueba1").append("lastName", "a")
				.append("email", "prueba1@prueba1.com")
				.append("password", "57420b1f0b1e2d07e407a04ff8bbc205a57b3055b34ed94658c04ed38f62daa7")
				.append("date","11/11/2002")
				.append("amount", 100).append("role", "standard");
		usuarios.insertOne(user1);
	}

	private void insertOffers() {
		MongoCollection<Document> offers = getMongodb().getCollection("offers");
		Document offer1 = new Document().append("title", "117").append("author", "prueba2@prueba2.com")
				.append("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaa")
				.append("price",5)
				.append("date","11/11/2002")
				.append("purchase",false)
				.append("buyer",null)
				.append("feature",false);
		offers.insertOne(offer1);
		Document offer2 = new Document().append("title", "118").append("author", "prueba2@prueba2.com")
				.append("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaa")
				.append("price",100)
				.append("date","11/11/2002")
				.append("purchase",false)
				.append("buyer",null)
				.append("feature",false);
		offers.insertOne(offer2);
		Document offer3 = new Document().append("title", "119").append("author", "prueba2@prueba2.com")
				.append("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaa")
				.append("price",150)
				.append("date","11/11/2002")
				.append("purchase",false)
				.append("buyer",null)
				.append("feature",false);
		offers.insertOne(offer3);
	}
}
