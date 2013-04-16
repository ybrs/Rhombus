package com.pardot.service.tools.cobject;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;

public class CKeyspaceDefinition {
	private String name;
	private String replicationClass;
	private int replicationFactor;
	private Collection<CDefinition> definitions;

	public CKeyspaceDefinition() {

	}

	public static CKeyspaceDefinition fromJsonString(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, CKeyspaceDefinition.class);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<CDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(Collection<CDefinition> definitions) {
		this.definitions = definitions;
	}

	public String getReplicationClass() {
		return replicationClass;
	}

	public void setReplicationClass(String replicationClass) {
		this.replicationClass = replicationClass;
	}

	public int getReplicationFactor() {
		return replicationFactor;
	}

	public void setReplicationFactor(int replicationFactor) {
		this.replicationFactor = replicationFactor;
	}

}
