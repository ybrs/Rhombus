package com.pardot.analyticsservice.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.pardot.analyticsservice.cassandra.cobject.CKeyspaceDefinition;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class ConnectionManager {

	private List<String> contactPoints;
	private Map<CKeyspaceDefinition, ObjectMapper> objectMappers = Maps.newHashMap();
	private CKeyspaceDefinition defaultKeyspace;
	private Cluster cluster;

	public ConnectionManager(Properties properties) {
		this.contactPoints = Lists.newArrayList(properties.getProperty("contactPoints").split(","));
		buildCluster();
	}

	public ConnectionManager(Properties properties, CKeyspaceDefinition defaultKeyspace) {
		this.contactPoints = Lists.newArrayList(properties.getProperty("contactPoints").split(","));
		this.defaultKeyspace = defaultKeyspace;
		buildCluster();
	}

	private void buildCluster() {
		Cluster.Builder builder = Cluster.builder();
		for(String contactPoint : contactPoints) {
			builder.addContactPoint(contactPoint);
		}
		cluster = builder.build();
	}

	/**
	 * Get the default object mapper
	 * @return The default object mapper
	 */
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = objectMappers.get(defaultKeyspace);
		if(objectMapper == null) {
			Session session = cluster.connect();
			objectMapper = new ObjectMapper(session, defaultKeyspace);
			objectMappers.put(defaultKeyspace, objectMapper);
		}
		return objectMapper;
	}

	/**
	 * This method rebuilds a keyspace from a definition.  In the process
	 * it removes any existing keyspace with the same name.  This operation
	 * is immediate and irreversible.
	 *
	 * @param keyspaceDefinition
	 */
	public void rebuildKeyspace(CKeyspaceDefinition keyspaceDefinition) {
		//Get a session for the new keyspace
		Session session = getSessionForNewKeyspace(keyspaceDefinition);
		//Use this session to create an object mapper and build the keyspace
		ObjectMapper mapper = new ObjectMapper(session, keyspaceDefinition);
		mapper.buildKeyspace();
		defaultKeyspace = keyspaceDefinition;
		objectMappers.put(defaultKeyspace, mapper);
	}

	/**
	 * Create and return a new session for the specified cluster.
	 * The caller is responsible for terminating the session.
	 * @return Empty session
	 */
	public Session getEmptySession() {
		return cluster.connect();
	}

	private Session getSessionForNewKeyspace(CKeyspaceDefinition keyspace) {
		//Get a new session
		Session session = cluster.connect();

		//Drop the keyspace if it already exists
		try {
			session.execute("DROP KEYSPACE " + keyspace.getName() + ";");
		} catch(Exception e) {
			//Ignore
		}

		//Create the new keyspace
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE KEYSPACE ");
		sb.append(keyspace.getName());
		sb.append(" WITH replication = { 'class' : '");
		sb.append(keyspace.getReplicationClass());
		sb.append("', 'replication_factor' : ");
		sb.append("" + keyspace.getReplicationFactor());
		sb.append("};");
		session.execute(sb.toString());

		//Close our session and get a new one directly associated with the new keyspace
		session.shutdown();
		session = cluster.connect(keyspace.getName());
		return session;
	}

	/**
	 * Teardown all connections contained in associated object mappers
	 */
	public void teardown() {
		for(ObjectMapper mapper : objectMappers.values()) {
			mapper.teardown();
		}
	}
}
