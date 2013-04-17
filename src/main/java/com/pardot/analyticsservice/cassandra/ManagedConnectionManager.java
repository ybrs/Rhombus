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
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void stop() throws Exception {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
