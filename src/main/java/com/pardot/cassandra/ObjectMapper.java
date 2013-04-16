package com.pardot.cassandra;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.common.collect.Maps;
import com.pardot.service.tools.cobject.*;

import java.util.Map;

public class ObjectMapper {

	private Session session;
	private CKeyspaceDefinition keyspaceDefinition;
	private CObjectCQLGenerator cqlGenerator;

	public ObjectMapper(Session session, CKeyspaceDefinition keyspaceDefinition) {
		//This expects a session without an associated keyspace
		this.session = session;
		this.keyspaceDefinition = keyspaceDefinition;
		Map<String, CDefinition> definitionMap = Maps.newHashMap();
		for(CDefinition definition : this.keyspaceDefinition.getDefinitions()) {
			definitionMap.put(definition.getName(), definition);
		}
		this.cqlGenerator = new CObjectCQLGenerator(definitionMap);
	}

	/**
	 * Build the tables contained in the keyspace definition.
	 * This method assumes that its keyspace exists and
	 * does not contain any tables.
	 */
	public void buildKeyspace() {
		for(CDefinition definition : this.keyspaceDefinition.getDefinitions()) {
			CQLStatementIterator statementIterator = cqlGenerator.makeCQLforCreate(definition.getName());
			while(statementIterator.hasNext()) {
				String cql = statementIterator.next();
				session.execute(cql);
			}
		}
	}

	public void insert(String objectType, Map<String, String> values) throws CQLGenerationException {
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforInsert(objectType, values);
		while(statementIterator.hasNext()) {
			String cql = statementIterator.next();
			session.execute(cql);
		}
	}

	public void teardown() {
		session.shutdown();
	}
}
