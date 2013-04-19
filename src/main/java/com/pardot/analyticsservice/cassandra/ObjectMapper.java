package com.pardot.analyticsservice.cassandra;


import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pardot.analyticsservice.cassandra.cobject.*;
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
	private static final boolean logCql = true;

	private Session session;
	private CKeyspaceDefinition keyspaceDefinition;
	private CObjectCQLGenerator cqlGenerator;

	public ObjectMapper(Session session, CKeyspaceDefinition keyspaceDefinition) {
		this.session = session;
		this.keyspaceDefinition = keyspaceDefinition;
		this.cqlGenerator = new CObjectCQLGenerator(keyspaceDefinition.getDefinitions(),null);
	}

	/**
	 * Build the tables contained in the keyspace definition.
	 * This method assumes that its keyspace exists and
	 * does not contain any tables.
	 */
	public void buildKeyspace() {
		//First build the shard index
		String cql = CObjectCQLGenerator.makeCQLforShardIndexTableCreate();
		executeCql(cql);
		//Now build the tables for each object
		for(CDefinition definition : keyspaceDefinition.getDefinitions().values()) {
			CQLStatementIterator statementIterator = cqlGenerator.makeCQLforCreate(definition.getName());
			while(statementIterator.hasNext()) {
				cql = statementIterator.next();
				executeCql(cql);
			}
		}
	}

	private void executeCql(String cql) {
		if(logCql) {
			logger.debug("Executing CQL: {}", cql);
		}
		session.execute(cql);
	}

	public UUID insert(String objectType, Map<String, String> values) throws CQLGenerationException {
		logger.debug("Insert {}", objectType);
		System.out.println("Insert object " + objectType);
		UUID key = UUIDs.timeBased();
		long timestamp = UUIDs.unixTimestamp(key);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforInsert(objectType, values, key, timestamp);
		while(statementIterator.hasNext()) {
			String cql = statementIterator.next();
			executeCql(cql);
		}
		return key;
	}

	/**
	 * Delete objecttype with id key
	 * @param objectType
	 * @param key
	 */
	public void delete(String objectType, UUID key) {
		CDefinition def = keyspaceDefinition.getDefinitions().get(objectType);
		Map<String, String> values = getByKey(objectType, key);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforDelete(objectType, key, values, 0);
		List<Map<String, String>> results = mapResults(statementIterator, def, 0L);
	}

	/**
	 *
	 * @param objectType
	 * @param key
	 * @return Object of type with key or null if it does not exist
	 */
	public Map<String, String> getByKey(String objectType, UUID key) {
		CDefinition def = keyspaceDefinition.getDefinitions().get(objectType);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforGet(objectType, key);
		List<Map<String, String>> results = mapResults(statementIterator, def, 1L);
		if(results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @param objectType
	 * @param criteria
	 * @return List of objects that match the specified type and criteria
	 * @throws CQLGenerationException
	 */
	public List<Map<String, String>> list(String objectType, Criteria criteria) throws CQLGenerationException {
		CDefinition def = keyspaceDefinition.getDefinitions().get(objectType);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforGet(objectType, criteria);
		List<Map<String, String>> results = mapResults(statementIterator, def, criteria.getLimit());
		return results;
	}


	/**
	 * Iterates through cql statements executing them in sequence and mapping the results until limit is reached
	 * @param statementIterator
	 * @param definition
	 * @return Ordered resultset concatenating results from statements in statement iterator.
	 */
	private List<Map<String, String>> mapResults(CQLStatementIterator statementIterator, CDefinition definition, Long limit) {
		List<Map<String, String>> results = Lists.newArrayList();
		int statementNumber = 0;
		int resultNumber = 0;
		while(statementIterator.hasNext(resultNumber) ) {
			String cql = statementIterator.next();
			logger.debug("Executing CQL: " + cql);
			ResultSet resultSet = session.execute(cql);
			for(Row row : resultSet) {
				Map<String, String> result = mapResult(row, definition);
				results.add(result);
				resultNumber++;
			}
			statementNumber++;
			if((limit > 0 && resultNumber >= limit) || statementNumber > reasonableStatementLimit) {
				break;
			}
		}
		return results;
	}

	/**
	 * @param row
	 * @param definition
	 * @return Data contained in a row mapped to the object described in definition.
	 */
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
