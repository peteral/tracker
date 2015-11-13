package rest;

import static rest.Constants.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@RequestScoped
@Path("/public")
@SuppressWarnings("unchecked")
public class ReportResource {

	private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

	@GET
	@Path("report/{id}")
	public void report(@PathParam("id") String id) {
		try (MongoClient client = new MongoClient(new MongoClientURI(CONNECT_STRING))) {

			MongoDatabase database = client.getDatabase(DATABASE_NAME);
			MongoCollection<Document> collection = database.getCollection(TRACKING);

			Document filter = new Document(CLIENT_ID, id);
			Document clientData = collection.find(filter).first();

			long now = System.currentTimeMillis();
			// FIXME server running in the US - need to change time zone to EUW
			// here, otherwise sessions will be assigned to wrong days
			String dayString = dayFormat.format(new Date());

			if (clientData == null) {
				clientData = createNewClient(id, now, dayString);
				collection.insertOne(clientData);
			}

			Document day = findCurrentDay(clientData, now, dayString);
			updateCurrentSession(now, day);

			updateDuration(day, SESSIONS);
			updateDuration(clientData, DAYS);

			collection.replaceOne(filter, clientData);
		}
	}

	private Document createNewClient(String id, long now, String dayString) {
		Document clientData;
		List<Object> days = new ArrayList<>();
		clientData = new Document(CLIENT_ID, id).append(DAYS, days);
		return clientData;
	}

	private List<Object> updateCurrentSession(long now, Document day) {
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
		return sessions;
	}

	private Document findCurrentDay(Document clientData, long now, String dayString) {
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
		return day;
	}

	private void updateDuration(Document document, String key) {
		List<Object> entries = (List<Object>) document.get(key);

		long total = 0L;
		for (Object obj : entries) {
			Document doc = (Document) obj;
			total += doc.getLong(DURATION);
		}
		document.append(DURATION, total);
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
