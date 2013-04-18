package com.pardot.analyticsservice.resources;


import com.pardot.analyticsservice.AnalyticsServiceConfiguration;
import com.pardot.analyticsservice.cassandra.ConnectionManager;
import com.pardot.analyticsservice.core.AnalyticsDataProvider;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */

@Path("/analyticsdata")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnalyticsDataResource {

	private AnalyticsServiceConfiguration analyticsServiceConfiguration;
	private AnalyticsDataProvider dataProvider;

	public AnalyticsDataResource(AnalyticsServiceConfiguration cassandraConfiguration, AnalyticsDataProvider dataProvider) {
		this.analyticsServiceConfiguration = cassandraConfiguration;
		this.dataProvider = dataProvider;
	}

	@Path("/configuration")
	@GET
	@Timed
	public AnalyticsServiceConfiguration showConfiguration() {
		return analyticsServiceConfiguration;
	}

	@Path("/{object}/{id}")
	@GET
	@Timed
	public Map<String, String> getObjectById(@PathParam("object") String object, @PathParam("id") String id) {
		return dataProvider.doGet(object, id);
	}

	@Path("/{object}")
	@GET
	@Timed
	public List<Map<String, String>> queryObject(@PathParam("object") String object, @Context UriInfo uriInfo) {
		return dataProvider.doQuery(object, uriInfo.getQueryParameters());
	}

	@Path("/{object}")
	@PUT
	@Timed
	public String putObject(@PathParam("object") String object, Map<String, String> values) {
		return dataProvider.doInsert(object, values);
	}
}
