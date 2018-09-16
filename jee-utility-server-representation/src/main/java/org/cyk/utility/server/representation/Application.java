package org.cyk.utility.server.representation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

//@ApplicationPath("/")
//@Path("/")
//@ApplicationScoped
//TODO should be moved to another project folder called representation-impl like persistence-impl
public class Application extends javax.ws.rs.core.Application {

	@GET
	@Path("/")
	@Produces(javax.ws.rs.core.MediaType.TEXT_PLAIN)
	public String get() {
		//TODO this message could come from database
		return "Your API is running. Time is "+new java.util.Date();
	}
	
}