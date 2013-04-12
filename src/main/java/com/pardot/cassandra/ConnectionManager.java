package com.pardot.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConnectionManager {

	private List<String> contactPoints;
	private Map<String, Session> sessions = Maps.newHashMap();
	private Session defaultSession;
	private String defaultKeyspace;

	private Cluster cluster;

	public ConnectionManager(Properties properties) {
		setContactPoints(Lists.newArrayList(properties.getProperty("contactPoints").split(",")));
		setDefaultKeyspace(properties.getProperty("defaultKeyspace"));
		buildCluster();
	}

	private void buildCluster() {
		Cluster.Builder builder = Cluster.builder();
		for(String contactPoint : contactPoints) {
			builder.addContactPoint(contactPoint);
		}
		cluster = builder.build();
		if(defaultKeyspace != null) {
			defaultSession = getSession(defaultKeyspace);
		}
	}

	/**
	 * Get a session without an associated keyspace
	 * The requester is responsible for closing this session
	 * @return A session for this connection
	 */
	public Session getSession() {
		return getCluster().connect();
	}

	/**
	 * Get the session created for the default keyspace
	 * @return The default session for this connection
	 */
	public Session getDefaultSession() {
		return defaultSession;
	}

	/**
	 * Get a session for the specified defaultKeyspace
	 * @param keyspace
	 * @return a session for the specified defaultKeyspace
	 */
	public Session getSession(String keyspace) {
		Session session = sessions.get(keyspace);
		if(session == null) {
			session = getCluster().connect(keyspace);
			sessions.put(keyspace, session);
		}
		return session;
	}

	private Cluster getCluster() {
		return cluster;
	}

	public List<String> getContactPoints() {
		return contactPoints;
	}

	public void setContactPoints(List<String> contactPoints) {
		this.contactPoints = contactPoints;
	}

	public String getDefaultKeyspace() {
		return defaultKeyspace;
	}

	public void setDefaultKeyspace(String defaultKeyspace) {
		this.defaultKeyspace = defaultKeyspace;
	}
}
