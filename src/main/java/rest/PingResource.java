package rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@RequestScoped
@Path("/ping")
@Produces("application/json")
@Consumes("application/json")
public class PingResource {

	@GET
	public String ping() {
		try (MongoClient client = new MongoClient("127.5.253.2")) {

			MongoDatabase database = client.getDatabase("peteral");

			MongoCollection<Document> collection = database.getCollection("test");

			return collection.find().first().toJson();
		}
	}
}
