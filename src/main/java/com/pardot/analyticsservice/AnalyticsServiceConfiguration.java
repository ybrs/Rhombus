package com.pardot.analyticsservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pardot.analyticsservice.cassandra.CassandraConfiguration;
import com.yammer.dropwizard.config.Configuration;

import javax.validation.constraints.NotNull;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class AnalyticsServiceConfiguration extends Configuration {
	@NotNull
	@JsonProperty
	private CassandraConfiguration cassandraConfiguration;

	@NotNull
	@JsonProperty
	private String environment;

	public CassandraConfiguration getCassandraConfiguration() {
		return cassandraConfiguration;
	}

	public void setCassandraConfiguration(CassandraConfiguration cassandraConfiguration) {
		this.cassandraConfiguration = cassandraConfiguration;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
}
