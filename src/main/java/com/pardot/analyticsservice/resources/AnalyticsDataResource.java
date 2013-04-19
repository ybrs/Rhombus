package com.pardot.analyticsservice.resources;


import com.pardot.analyticsservice.AnalyticsServiceConfiguration;
import com.pardot.analyticsservice.core.AnalyticsDataProvider;
import com.yammer.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(AnalyticsDataResource.class);

	private AnalyticsServiceConfiguration analyticsServiceConfiguration;
	private AnalyticsDataProvider dataProvider;

	public AnalyticsDataResource(AnalyticsServiceConfiguration cassandraConfiguration, AnalyticsDataProvider dataProvider) {
		this.analyticsServiceConfiguration = cassandraConfiguration;
		this.dataProvider = dataProvider;
	}

	@Path("/_configuration")
	 @GET
	 @Timed
	public AnalyticsResult showConfiguration() {
		AnalyticsResult result = new AnalyticsResult();
		result.setSuccess(true);
		result.setResult(analyticsServiceConfiguration);
		return result;
	}

	@Path("/_rebuildkeyspace")
	@GET
	@Timed
	public AnalyticsResult rebuildKeyspace() {
		AnalyticsResult result = new AnalyticsResult();
		try {
			dataProvider.rebuildKeyspace();
			result.setSuccess(true);
		} catch(Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@Path("/{object}/{id}")
	@GET
	@Timed
	public AnalyticsResult getObjectById(@PathParam("object") String object, @PathParam("id") String id) {
		logger.debug("getObjectById {}:{}", object, id);
		AnalyticsResult result = new AnalyticsResult();
		try {
			Map<String,String> returnedObject = dataProvider.doGet(object, id);
			result.setResult(returnedObject);
			result.setSuccess(true);
		} catch(Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@Path("/{object}")
	@GET
	@Timed
	public AnalyticsResult queryObject(@PathParam("object") String object, @Context UriInfo uriInfo) {
		logger.debug("queryObject {}", object);
		AnalyticsResult result = new AnalyticsResult();
		try {
			List<Map<String, String>> returnedObjects = dataProvider.doQuery(object, uriInfo.getQueryParameters());
			result.setSuccess(true);
			result.setResult(returnedObjects);
		} catch(Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@Path("/{object}")
	@PUT
	@Timed
	public AnalyticsResult insert(@PathParam("object") String object, Map<String, String> values) {
		logger.debug("insert object {}", object);
		AnalyticsResult result = new AnalyticsResult();
		try {
			String id = dataProvider.doInsert(object, values);
			result.setSuccess(true);
			result.setId(id);
		} catch(Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@Path("/{object}/{id}")
	@POST
	@Timed
	public AnalyticsResult update(@PathParam("object") String object, @PathParam("id") String id, Map<String, String> values) {
		logger.debug("Update object {}: {}", object, id);
		AnalyticsResult result = new AnalyticsResult();
		try {
			String newId = dataProvider.doUpdate(object, id, values);
			result.setSuccess(true);
			result.setId(newId);
		} catch(Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@Path("/{object}/{id}")
	@DELETE
	@Timed
	public AnalyticsResult delete(@PathParam("object") String object, @PathParam("id") String id) {
		logger.debug("Delete object {}: {}", object, id);
		AnalyticsResult result = new AnalyticsResult();
		try {
			dataProvider.doDelete(object, id);
			result.setSuccess(true);
		} catch(Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}
}
