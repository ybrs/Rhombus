package com.pardot.cassandra;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pardot.service.tools.cobject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class ObjectMapper {

	private static Logger logger = LoggerFactory.getLogger(ObjectMapper.class);
	private static final int reasonableStatementLimit = 1;

	private Session session;
	private CKeyspaceDefinition keyspaceDefinition;
	private CObjectCQLGenerator cqlGenerator;

	public ObjectMapper(Session session, CKeyspaceDefinition keyspaceDefinition) {
		this.session = session;
		this.keyspaceDefinition = keyspaceDefinition;
		this.cqlGenerator = new CObjectCQLGenerator(keyspaceDefinition.getDefinitions());
	}

	/**
	 * Build the tables contained in the keyspace definition.
	 * This method assumes that its keyspace exists and
	 * does not contain any tables.
	 */
	public void buildKeyspace() {
		for(CDefinition definition : keyspaceDefinition.getDefinitions().values()) {
			CQLStatementIterator statementIterator = cqlGenerator.makeCQLforCreate(definition.getName());
			while(statementIterator.hasNext()) {
				String cql = statementIterator.next();
				logger.debug("Executing CQL: " + cql);
				session.execute(cql);
			}
		}
	}

	public UUID insert(String objectType, Map<String, String> values) throws CQLGenerationException {
		UUID key = UUIDs.timeBased();
		long timestamp = UUIDs.unixTimestamp(key);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforInsert(objectType, values, key, timestamp);
		while(statementIterator.hasNext()) {
			String cql = statementIterator.next();
			logger.debug("Executing CQL: " + cql);
			session.execute(cql);
		}
		return key;
	}

	public Map<String, String> getByKey(String objectType, UUID key) {
		CDefinition def = keyspaceDefinition.getDefinitions().get(objectType);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforGet(objectType, key);
		List<Map<String, String>> results = mapResults(statementIterator, def);
		if(results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	public List<Map<String, String>> list(String objectType, Criteria criteria) throws CQLGenerationException {
		CDefinition def = keyspaceDefinition.getDefinitions().get(objectType);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforGet(objectType, criteria);
		List<Map<String, String>> results = mapResults(statementIterator, def);
		return results;
	}

	private List<Map<String, String>> mapResults(CQLStatementIterator statementIterator, CDefinition definition) {
		List<Map<String, String>> results = Lists.newArrayList();
		int statementNumber = 0;
		while(statementIterator.hasNext() && statementNumber < reasonableStatementLimit) {
			String cql = statementIterator.next();
			logger.debug("Executing CQL: " + cql);
			ResultSet resultSet = session.execute(cql);
			for(Row row : resultSet) {
				Map<String, String> result = mapResult(row, definition);
				results.add(result);
			}
			statementNumber++;
		}
		return results;
	}

	private Map<String, String> mapResult(Row row, CDefinition definition) {
		Map<String, String> result = Maps.newHashMap();
		for(CField field : definition.getFields().values()) {
			result.put(field.getName(), getFieldValue(row, field));
		}
		return result;
	}

	private String getFieldValue(Row row, CField field) {
		Object fieldValue;
		switch(field.getType()) {
			case ASCII:
			case VARCHAR:
			case TEXT:
				fieldValue = row.getString(field.getName());
				break;
			case BIGINT:
			case COUNTER:
				fieldValue = row.getLong(field.getName());
				break;
			case BLOB:
				fieldValue = row.getBytes(field.getName());
				break;
			case BOOLEAN:
				fieldValue = row.getBool(field.getName());
				break;
			case DECIMAL:
				fieldValue = row.getDecimal(field.getName());
				break;
			case DOUBLE:
				fieldValue = row.getDouble(field.getName());
				break;
			case FLOAT:
				fieldValue = row.getFloat(field.getName());
				break;
			case INT:
				fieldValue = row.getInt(field.getName());
				break;
			case TIMESTAMP:
				fieldValue = row.getDate(field.getName());
				break;
			case UUID:
			case TIMEUUID:
				fieldValue = row.getUUID(field.getName());
				break;
			case VARINT:
				fieldValue = row.getVarint(field.getName());
				break;
			default:
				fieldValue = null;
		}
		return fieldValue.toString();
	}

	public void teardown() {
		session.shutdown();
	}
}
