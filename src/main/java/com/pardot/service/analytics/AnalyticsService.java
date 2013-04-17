package com.pardot.service.analytics;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class AnalyticsService extends Service<AnalyticsServiceConfiguration> {

	public static void main(String[] args) throws Exception {
		new AnalyticsService().run(args);
	}

	@Override
	public void initialize(Bootstrap<AnalyticsServiceConfiguration> bootstrap) {
		bootstrap.setName("analytics-service");
	}

	@Override
	public void run(AnalyticsServiceConfiguration configuration, Environment environment) throws Exception {
		environment.addResource(new AnalyticsDataResource(configuration.getCassandraConfiguration()));
		environment.addHealthCheck(new ServiceHealthCheck());
	}
}
