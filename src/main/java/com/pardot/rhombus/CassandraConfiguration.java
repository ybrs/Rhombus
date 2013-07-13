package com.pardot.rhombus;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class CassandraConfiguration {

	@NotNull
	@JsonProperty
	private List<String> contactPoints;

	@NotNull
	@JsonProperty
	private Integer consistencyHorizion;

	@JsonProperty
	private String localDatacenter;


	public List<String> getContactPoints() {
		return contactPoints;
	}

	public void setContactPoints(List<String> contactPoints) {
		this.contactPoints = contactPoints;
	}

	public String getLocalDatacenter() {
		return localDatacenter;
	}

	public void setLocalDatacenter(String localDatacenter) {
		this.localDatacenter = localDatacenter;
	}

	public Integer getConsistencyHorizion() {
		return consistencyHorizion;
	}

	public void setConsistencyHorizion(Integer consistencyHorizion) {
		this.consistencyHorizion = consistencyHorizion;
	}
}
