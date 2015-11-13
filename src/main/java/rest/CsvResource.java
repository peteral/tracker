package rest;

import static rest.Constants.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@RequestScoped
@Path("/protected")
@SuppressWarnings("unchecked")
public class CsvResource {

	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmm");

	@Path("csv")
	@Produces("text/csv")
	@GET
	public Response list() {
		try (MongoClient client = new MongoClient(new MongoClientURI(CONNECT_STRING))) {

			StringBuilder result = new StringBuilder("Client, Day, Minutes");
			result.append('\n');

			MongoDatabase database = client.getDatabase(DATABASE_NAME);
			MongoCollection<Document> collection = database.getCollection(TRACKING);

			collection.find().forEach((Consumer<Document>) doc -> {
				String clientId = doc.getString(CLIENT_ID);
				List<Object> days = (List<Object>) doc.get(DAYS);
				for (Object obj : days) {
					Document day = (Document) obj;
					result.append(clientId);
					result.append(", ");
					result.append(day.getString(DAY));
					result.append(", ");
					result.append(day.getLong(DURATION));
					result.append('\n');
				}
			});

			ResponseBuilder response = Response.ok(result.toString());
			response.header("Content-Disposition",
					"attachment; filename=\"tracking-" + format.format(new Date()) + "\"");

			return response.build();
		}
	}

}
