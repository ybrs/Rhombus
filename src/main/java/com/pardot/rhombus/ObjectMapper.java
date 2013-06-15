package com.pardot.rhombus;


import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pardot.rhombus.cobject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
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
	private static final int reasonableStatementLimit = 20;
	private boolean logCql = false;
	private Map<String,BoundStatement> boundStatementCache;

	private Session session;
	private CKeyspaceDefinition keyspaceDefinition;
	private CObjectCQLGenerator cqlGenerator;

	public ObjectMapper(Session session, CKeyspaceDefinition keyspaceDefinition) {
		this.boundStatementCache = Maps.newHashMap();
		this.session = session;
		this.keyspaceDefinition = keyspaceDefinition;
		this.cqlGenerator = new CObjectCQLGenerator(keyspaceDefinition.getDefinitions(),null);
	}

	/**
	 * Build the tables contained in the keyspace definition.
	 * This method assumes that its keyspace exists and
	 * does not contain any tables.
	 */
	public void buildKeyspace(Boolean forceRebuild) {
		//First build the shard index
		CQLStatement cql = CObjectCQLGenerator.makeCQLforShardIndexTableCreate();
		try {
			executeCql(cql);
		} catch(Exception e) {
			if(forceRebuild) {
				CQLStatement dropCql = CObjectCQLGenerator.makeCQLforShardIndexTableDrop();
				logger.debug("Attempting to drop table with cql {}", dropCql);
				executeCql(dropCql);
				executeCql(cql);
			} else {
				logger.debug("Not dropping shard index table");
			}
		}
		//Now build the tables for each object if the definition contains tables
		if(keyspaceDefinition.getDefinitions() != null) {
			for(CDefinition definition : keyspaceDefinition.getDefinitions().values()) {
				CQLStatementIterator statementIterator = cqlGenerator.makeCQLforCreate(definition.getName());
				CQLStatementIterator dropStatementIterator = cqlGenerator.makeCQLforDrop(definition.getName());
				while(statementIterator.hasNext()) {
					cql = statementIterator.next();
					CQLStatement dropCql = dropStatementIterator.next();
					try {
						executeCql(cql);
					} catch (AlreadyExistsException e) {
						if(forceRebuild) {
							logger.debug("ForceRebuild is on, dropping table");
							executeCql(dropCql);
							executeCql(cql);
						} else {
							logger.warn("Table already exists and will not be updated");
						}
					}
				}
			}
		}
	}

	/**
	 * This should never be used outside of testing
	 * @param cql String of cql to execute
	 */
	public ResultSet executeCql(CQLStatement cql) {
		if(logCql) {
			logger.debug("Executing CQL: {}", cql.getQuery());
			//TODO: log values
		}
		if(cql.isPreparable()){
			//Do prepared statement
			BoundStatement bs = boundStatementCache.get(cql.getQuery());
			if(bs == null){
				PreparedStatement statement = session.prepare(cql.getQuery());
				bs = new BoundStatement(statement);
				if(cql.isCacheable()){
					boundStatementCache.put(cql.getQuery(),bs);
				}
			}

			return session.execute(bs.bind(cql.getValues()));
		}
		else{
			//just run a normal execute without a prepared statement
			return session.execute(cql.getQuery());
		}
	}


	/**
	 * Insert a new object with values and key
	 * @param objectType Type of object to insert
	 * @param values Values to insert
	 * @param key Time UUID to use as key
	 * @return
	 * @throws CQLGenerationException
	 */
	public UUID insert(String objectType, Map<String, Object> values, UUID key) throws CQLGenerationException {
		logger.debug("Insert {}", objectType);
		if(key == null) {
			key = UUIDs.timeBased();
		}
		long timestamp = System.currentTimeMillis();
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforInsert(objectType, values, key, timestamp);
		while(statementIterator.hasNext()) {
			CQLStatement cql = statementIterator.next();
			executeCql(cql);
		}
		return key;
	}

	/**
	 * Insert a new objectType with values
	 * @param objectType Type of object to insert
	 * @param values Values to insert
	 * @return UUID of inserted object
	 * @throws CQLGenerationException
	 */
	public UUID insert(String objectType, Map<String, Object> values) throws CQLGenerationException {
		return insert(objectType, values, (UUID)null);
	}

	/**
	 * Used to insert an object with a UUID based on the provided timestamp
	 * Best used for testing, as time resolution collisions are not accounted for
	 * @param objectType Type of object to insert
	 * @param values Values to insert
	 * @param timestamp Timestamp to use to create the object UUID
	 * @return the UUID of the newly inserted object
	 */
	public UUID insert(String objectType, Map<String, Object> values, Long timestamp) throws CQLGenerationException {
		UUID uuid = UUIDs.startOf(timestamp);
		return insert(objectType, values, uuid);
	}

	/**
	 * Delete Object of type with id key
	 * @param objectType Type of object to delete
	 * @param key Key of object to delete
	 */
	public void delete(String objectType, UUID key) {
		CDefinition def = keyspaceDefinition.getDefinitions().get(objectType);
		Map<String, Object> values = getByKey(objectType, key);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforDelete(objectType, key, values, null);
		mapResults(statementIterator, def, 0L);
	}

	/**
	 * Update objectType with key using values
	 * @param objectType Type of object to update
	 * @param key Key of object to update
	 * @param values Values to update
	 * @return new UUID of the object
	 * @throws CQLGenerationException
	 */
	public UUID update(String objectType, UUID key, Map<String, Object> values) throws CQLGenerationException {
		//Make a new key
		UUID newKey = UUIDs.startOf(UUIDs.unixTimestamp(key));
		//Delete
		delete(objectType, key);
		//Insert
		return insert(objectType, values, newKey);
	}

	/**
	 *
	 * @param objectType Type of object to get
	 * @param key Key of object to get
	 * @return Object of type with key or null if it does not exist
	 */
	public Map<String, Object> getByKey(String objectType, UUID key) {
		CDefinition def = keyspaceDefinition.getDefinitions().get(objectType);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforGet(objectType, key);
		List<Map<String, Object>> results = mapResults(statementIterator, def, 1L);
		if(results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @param objectType Type of object to query
	 * @param criteria Criteria to query by
	 * @return List of objects that match the specified type and criteria
	 * @throws CQLGenerationException
	 */
	public List<Map<String, Object>> list(String objectType, Criteria criteria) throws CQLGenerationException {
		CDefinition def = keyspaceDefinition.getDefinitions().get(objectType);
		CQLStatementIterator statementIterator = cqlGenerator.makeCQLforGet(objectType, criteria);
		return mapResults(statementIterator, def, criteria.getLimit());
	}


	/**
	 * Iterates through cql statements executing them in sequence and mapping the results until limit is reached
	 * @param statementIterator Statement iterator to execute
	 * @param definition definition to execute the statements against
	 * @return Ordered resultset concatenating results from statements in statement iterator.
	 */
	private List<Map<String, Object>> mapResults(CQLStatementIterator statementIterator, CDefinition definition, Long limit) {
		List<Map<String, Object>> results = Lists.newArrayList();
		int statementNumber = 0;
		int resultNumber = 0;
		while(statementIterator.hasNext(resultNumber) ) {
			CQLStatement cql = statementIterator.next();
			logger.debug("Executing CQL: " + cql);
			ResultSet resultSet = executeCql(cql);
			for(Row row : resultSet) {
				Map<String, Object> result = mapResult(row, definition);
				results.add(result);
				resultNumber++;
			}
			statementNumber++;
			if((limit > 0 && resultNumber >= limit) || statementNumber > reasonableStatementLimit) {
				logger.debug("Breaking from mapping results");
				break;
			}
		}
		return results;
	}

	/**
	 * @param row The row to map
	 * @param definition The definition to map the row on to
	 * @return Data contained in a row mapped to the object described in definition.
	 */
	private Map<String, Object> mapResult(Row row, CDefinition definition) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("id", row.getUUID("id").toString());
		for(CField field : definition.getFields().values()) {
			result.put(field.getName(), getFieldValue(row, field));
		}
		return result;
	}

	private Object getFieldValue(Row row, CField field) {
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
				if(fieldValue != null) {
					fieldValue = ((Date)fieldValue).getTime();
				}
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
		return (fieldValue == null ? null : fieldValue);
	}

	public void setLogCql(boolean logCql) {
		this.logCql = logCql;
	}

	public void teardown() {
		session.shutdown();
	}
}
