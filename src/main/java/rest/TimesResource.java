package rest;

import java.util.function.Consumer;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@RequestScoped
@Path("/times")
public class TimesResource {

	@Path("list")
	@Produces("application/json")
	@GET
	public String ping() {
		try (MongoClient client = new MongoClient(
				new MongoClientURI("mongodb://admin:hxeJAeMTszW1@127.5.253.2/?authSource=peteral"))) {

			StringBuilder result = new StringBuilder("{ entries: [");
			result.append('\n');

			MongoDatabase database = client.getDatabase("peteral");
			MongoCollection<Document> collection = database.getCollection("tracking");

			collection.find().forEach((Consumer<Document>) doc -> {
				result.append(doc.toJson());
				result.append(",\n");
			});

			result.append('\n');
			result.append("]}");
			return result.toString();
		}
	}

	@GET
	@Path("report/{id}")
	public void report(@PathParam("id") String id) {
		try (MongoClient client = new MongoClient(
				new MongoClientURI("mongodb://admin:hxeJAeMTszW1@127.5.253.2/?authSource=peteral"))) {

			MongoDatabase database = client.getDatabase("peteral");
			MongoCollection<Document> collection = database.getCollection("tracking");

			collection.insertOne(new Document().append("clientId", id).append("time", System.currentTimeMillis()));
		}
	}
}
