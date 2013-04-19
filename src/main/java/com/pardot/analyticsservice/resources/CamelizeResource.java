package com.pardot.analyticsservice.resources;

import com.google.common.base.CaseFormat;
import com.pardot.analyticsservice.AnalyticsServiceConfiguration;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/19/13
 */
public class CamelizeResource {

	@Path("/_lowerUnderToCamel")
	@GET
	@Timed
	public String showConfiguration(String toCamelize) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, toCamelize);
	}
}
