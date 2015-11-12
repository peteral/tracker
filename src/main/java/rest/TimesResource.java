package rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

	private static final String DURATION = "duration";
	private static final String END = "end";
	private static final String START = "start";
	private static final String CONNECT_STRING = "mongodb://admin:hxeJAeMTszW1@127.5.253.2/?authSource=peteral";
	private static final String DATABASE_NAME = "peteral";
	private static final String TRACKING = "tracking";
	private static final String CLIENT_ID = "clientId";
	private static final String DAYS = "days";
	private static final String DAY = "day";
	private static final String SESSIONS = "sessions";
	private static final long TIMEOUT = 2 * 60 * 1000;
	private static final long MS_TO_MIN = 60 * 1000;
	private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Path("list")
	@Produces("application/json")
	@GET
	public String ping() {
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

	@GET
	@Path("report/{id}")
	public void report(@PathParam("id") String id) {
		try (MongoClient client = new MongoClient(new MongoClientURI(CONNECT_STRING))) {

			MongoDatabase database = client.getDatabase(DATABASE_NAME);
			MongoCollection<Document> collection = database.getCollection(TRACKING);

			Document filter = new Document(CLIENT_ID, id);
			Document clientData = collection.find(filter).first();

			long now = System.currentTimeMillis();
			String dayString = dayFormat.format(new Date());

			if (clientData == null) {
				Document day = createDay(now, dayString);
				List<Object> days = new ArrayList<>();
				days.add(day);
				clientData = new Document(CLIENT_ID, id).append(DAYS, days);
				collection.insertOne(clientData);
			} else {
				// look for current day
				List<Object> days = (List<Object>) clientData.get(DAYS);
				Document day = null;
				for (Object obj : days) {
					Document doc = (Document) obj;
					if (dayString.equals(doc.get(DAY))) {
						day = doc;
						break;
					}
				}

				if (day == null) {
					day = createDay(now, dayString);
					days.add(day);
				}

				// look for current session within day
				Document session = null;
				List<Object> sessions = (List<Object>) day.get(SESSIONS);
				for (Object obj : sessions) {
					Document doc = (Document) obj;
					if (doc.getLong(END) > now) {
						session = doc;
						break;
					}
				}

				if (session == null) {
					session = createSession(now);
					sessions.add(session);
				} else {
					session.append(END, now + TIMEOUT);
					session.append(DURATION, ((now + TIMEOUT) - session.getLong(START)) / MS_TO_MIN);
				}

				// calculate total duration
				long total = 0L;
				for (Object obj : sessions) {
					Document doc = (Document) obj;
					total += doc.getLong(DURATION);
				}
				day.append(DURATION, total);

				collection.replaceOne(filter, clientData);
			}
		}
	}

	private Document createDay(long now, String dayString) {
		Document session = createSession(now);

		List<Object> sessions = new ArrayList<>();
		sessions.add(session);

		Document day = new Document(DAY, dayString).append(DURATION, TIMEOUT / MS_TO_MIN).append(SESSIONS, sessions);
		return day;
	}

	private Document createSession(long now) {
		Document session = new Document(START, now).append(END, now + TIMEOUT).append(DURATION, TIMEOUT / MS_TO_MIN);
		return session;
	}
}
