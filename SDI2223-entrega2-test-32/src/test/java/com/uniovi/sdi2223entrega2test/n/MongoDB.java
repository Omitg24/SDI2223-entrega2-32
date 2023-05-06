package com.uniovi.sdi2223entrega2test.n;

import com.mongodb.client.FindIterable;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MongoDB {
	private MongoClient mongoClient;
	private MongoDatabase mongodb;

	Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
	SecretKeySpec secretKeySpec;
	private String secretKey = "abcdefg";

	public MongoDB() throws NoSuchAlgorithmException, InvalidKeyException {
		secretKeySpec= new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		hmacSHA256.init(secretKeySpec);
	}

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
				.append("date","11/11/2002")
				.append("amount", 100).append("role", "admin");
		usuarios.insertOne(admin);
		for(int i = 1; i < 16; i++) {
			String name = "user" + (i<10?"0":"") + i;
			usuarios.insertOne(new Document().append("name", name).append("lastName", "a")
					.append("email", name+"@email.com")
					.append("password", getHash(name))
					.append("date","11/11/2002")
					.append("amount", i==3?0:100).append("role", "standard"));

		}
	}

	private void insertOffers() {
		MongoCollection<Document> offers = getMongodb().getCollection("offers");
		Document offer1 = new Document().append("title", "117").append("author", "user02@email.com")
				.append("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaa")
				.append("price",5)
				.append("date","11/11/2002")
				.append("purchase",false)
				.append("buyer",null)
				.append("feature",false);
		offers.insertOne(offer1);
		Document offer2 = new Document().append("title", "118").append("author", "user02@email.com")
				.append("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaa")
				.append("price",100)
				.append("date","11/11/2002")
				.append("purchase",false)
				.append("buyer",null)
				.append("feature",false);
		offers.insertOne(offer2);
		Document offer3 = new Document().append("title", "119").append("author", "user02@email.com")
				.append("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaa")
				.append("price",150)
				.append("date","11/11/2002")
				.append("purchase",false)
				.append("buyer",null)
				.append("feature",false);
		offers.insertOne(offer3);
		for(int i = 1; i < 16; i++) {
			String name = "user" + (i<10?"0":"") + i + "@email.com";
			for (int j=0;j<10;j++) {
				offers.insertOne(new Document().append("title", i+""+j)
						.append("author", name)
						.append("description", "Oferta nueva")
						.append("price", 50)
						.append("date","11/11/2002")
						.append("purchase",false)
						.append("buyer",null)
						.append("feature",false));
			}

		}
	}

}
