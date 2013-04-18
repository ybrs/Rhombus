package com.pardot.analyticsservice.cassandra.cobject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public class CKeyspaceDefinition {
	private String name;
	private String replicationClass;
	private int replicationFactor;
	private Map<String, CDefinition> definitions;

	public CKeyspaceDefinition() {

	}

	public static CKeyspaceDefinition fromJsonString(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, CKeyspaceDefinition.class);
	}

	public static CKeyspaceDefinition fromJsonFile(String filename) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		InputStream inputStream = CKeyspaceDefinition.class.getClassLoader().getResourceAsStream(filename);
		return mapper.readValue(inputStream, CKeyspaceDefinition.class);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, CDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(Collection<CDefinition> definitions) {
		this.definitions = Maps.newHashMap();
		for(CDefinition def : definitions) {
			this.definitions.put(def.getName(), def);
		}
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
