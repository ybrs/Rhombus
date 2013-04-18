package com.pardot.analyticsservice;

import com.pardot.analyticsservice.cassandra.ManagedConnectionManager;
import com.pardot.analyticsservice.core.AnalyticsDataProvider;
import com.pardot.analyticsservice.health.ServiceHealthCheck;
import com.pardot.analyticsservice.resources.AnalyticsDataResource;
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
		ManagedConnectionManager cm = new ManagedConnectionManager(configuration.getCassandraConfiguration());
		//Add managed objects
		environment.manage(cm);

		//Add resources
		environment.addResource(new AnalyticsDataResource(configuration, new AnalyticsDataProvider(cm)));

		//Add health checks
		environment.addHealthCheck(new ServiceHealthCheck());

	}
}
