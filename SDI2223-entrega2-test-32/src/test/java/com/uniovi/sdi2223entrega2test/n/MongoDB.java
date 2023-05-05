package com.uniovi.sdi2223entrega2test.n;

import java.text.ParseException;
import java.util.Date;

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
		insertUsuarios();
		insertarOfertasYConversaciones();
	}

	private void deleteData() {
		getMongodb().getCollection("offers").drop();
		getMongodb().getCollection("users").drop();
		getMongodb().getCollection("conversations").drop();
		getMongodb().getCollection("messages").drop();
	}

	private void insertUsuarios() {
		MongoCollection<Document> usuarios = getMongodb().getCollection("users");
		Document admin = new Document().append("nombre", "admin").append("apellidos", "admin")
				.append("email", "admin@email.com")
				.append("password", "ebd5359e500475700c6cc3dd4af89cfd0569aa31724a1bf10ed1e3019dcfdb11")
				.append("amount", 100).append("rol", "admin");
		usuarios.insertOne(admin);

		Document user1 = new Document().append("nombre", "user1").append("apellidos", "user")
				.append("email", "user1@email.com")
				.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
				.append("amount", 100).append("rol", "user");
		usuarios.insertOne(user1);

		
	}

	private void insertarOfertasYConversaciones() {

	}

}
