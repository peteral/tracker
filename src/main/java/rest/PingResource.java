package rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@RequestScoped
@Path("/ping")
@Produces("application/json")
@Consumes("application/json")
public class PingResource {
	@GET
	public String ping() {
		return "{'ping' : 'pong'}";
	}
}
