package com.pardot.cassandra;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.pardot.service.tools.cobject.CKeyspaceDefinition;

public class ObjectMapper {

	private Session session;
	private CKeyspaceDefinition keyspaceDefinition;

	public ObjectMapper(Session session, CKeyspaceDefinition keyspaceDefinition) {
		//This expects a session without an associated keyspace
		this.session = session;
		this.keyspaceDefinition = keyspaceDefinition;
	}

	/**
	 * Build the tables contained in the keyspace definition.
	 * This method assumes that its keyspace exists and
	 * does not contain any tables.
	 */
	public void buildKeyspace() {

		//String cql = Subject.makeStaticTableCreate(def);
	}

	public void teardown() {
		session.shutdown();
	}
}
