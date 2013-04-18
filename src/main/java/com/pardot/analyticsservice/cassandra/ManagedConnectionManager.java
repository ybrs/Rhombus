package com.pardot.analyticsservice.cassandra;

import com.yammer.dropwizard.lifecycle.Managed;

import java.util.Properties;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class ManagedConnectionManager extends ConnectionManager implements Managed {

	public ManagedConnectionManager(CassandraConfiguration configuration) {
		super(configuration);
	}

	@Override
	public void start() throws Exception {
		super.buildCluster();
	}

	@Override
	public void stop() throws Exception {
		super.teardown();
	}
}
