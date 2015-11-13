package rest;

import static rest.Constants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@RequestScoped
@Path("/protected")
public class ListResource {

	@Path("list")
	@Produces("application/json")
	@GET
	public String list() {
		try (MongoClient client = new MongoClient(new MongoClientURI(CONNECT_STRING))) {

			StringBuilder result = new StringBuilder("{ \"entries\": [");
			result.append('\n');

			MongoDatabase database = client.getDatabase(DATABASE_NAME);
			MongoCollection<Document> collection = database.getCollection(TRACKING);

			List<String> entries = new ArrayList<>();
			collection.find().forEach((Consumer<Document>) doc -> {
				entries.add(doc.toJson());
			});

			for (int i = 0; i < entries.size(); i++) {
				result.append(entries.get(i));
				if (i < (entries.size() - 1)) {
					result.append(",\n");
				}
			}

			result.append('\n');
			result.append("]}");
			return result.toString();
		}
	}

}
