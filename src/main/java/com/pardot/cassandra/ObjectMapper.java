package com.pardot.cassandra;


import com.datastax.driver.core.Cluster;

import java.util.List;

public class ObjectMapper {

	public ObjectMapper(CKeyspace keyspace) {

	}

	/**
	 * Get a session without an associated keyspace
	 * The requester is responsible for closing this session
	 * @return A session for this connection
	 */
	protected ObjectMapper getEmptySession() {
		return getCluster().connect();
	}

	/**
	 * Get the session created for the default keyspace
	 * @return The default session for this connection
	 */
	public ObjectMapper getDefaultSession() {
		return defaultSession;
	}

	/**
	 * Get a session for the specified keyspace
	 * @param keyspace
	 * @return a session for the specified keyspace
	 */
	public ObjectMapper getSession(String keyspace) {
		ObjectMapper objectMapper = sessions.get(keyspace);
		if(objectMapper == null) {
			objectMapper = getCluster().connect(keyspace);
			sessions.put(keyspace, objectMapper);
		}
		return objectMapper;
	}

	private Cluster getCluster() {
		return cluster;
	}

	public void closeConnections() {
		for(ObjectMapper objectMapper : sessions.values()) {
			try {
				objectMapper.shutdown();
			} catch(Exception e) {
				//Ignore
			}
		}

	}
}
