package com.pardot.analyticsservice.health;

import com.yammer.metrics.core.HealthCheck;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class ServiceHealthCheck extends HealthCheck {

	public ServiceHealthCheck() {
		super("service");
	}

	@Override
	protected Result check() throws Exception {
		return Result.healthy();
	}
}
