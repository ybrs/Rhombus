package com.pardot.rhombus.functional;


import com.google.common.collect.Maps;
import com.pardot.rhombus.ConnectionManager;
import com.pardot.rhombus.Criteria;
import com.pardot.rhombus.ObjectMapper;
import com.pardot.rhombus.helpers.TestHelpers;
import com.pardot.rhombus.cobject.CKeyspaceDefinition;
import com.pardot.rhombus.util.JsonUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class ObjectMapperITCase {

	private static Logger logger = LoggerFactory.getLogger(ObjectMapperITCase.class);

	@Test
	public void testObjectMapper() throws Exception {
		logger.debug("Starting testObjectMapper");

		//Build the connection manager
		ConnectionManager cm = getConnectionManager();

		//Build our keyspace definition object
		String json = TestHelpers.readFileToString(this.getClass(), "CKeyspaceTestData.js");
		CKeyspaceDefinition definition = CKeyspaceDefinition.fromJsonString(json);
		assertNotNull(definition);

		//Rebuild the keyspace and get the object mapper
		cm.buildKeyspace(definition, true);
		cm.setDefaultKeyspace(definition);
		ObjectMapper om = cm.getObjectMapper();

		//Get a test object to insert
		Map<String, Object> testObject = JsonUtil.rhombusMapFromJsonMap(TestHelpers.getTestObject(0), definition.getDefinitions().get("testtype"));
		UUID key = om.insert("testtype", testObject);

		//Query to get back the object from the database
		Map<String, Object> dbObject = om.getByKey("testtype", key);
		for(String dbKey : dbObject.keySet()) {
			//Verify that everything but the key is the same
			if(!dbKey.equals("id")) {
				assertEquals(testObject.get(dbKey), dbObject.get(dbKey));
			}
		}

		//Add another object with the same foreign key
		UUID key2 = om.insert("testtype", JsonUtil.rhombusMapFromJsonMap(TestHelpers.getTestObject(1), definition.getDefinitions().get("testtype")));

		//Query by foreign key
		Criteria criteria = TestHelpers.getTestCriteria(0);
		criteria.getIndexKeys().put("foreignid",((Integer)criteria.getIndexKeys().get("foreignid")).longValue());
		List<Map<String, Object>> dbObjects = om.list("testtype", criteria);
		assertEquals(2, dbObjects.size());

		//Remove one of the objects we added
		om.delete("testtype", key);

		//Re-query by foreign key
		dbObjects = om.list("testtype", criteria);
		assertEquals(1, dbObjects.size());

		//Update the values of one of the objects
		Map<String, Object> testObject2 = JsonUtil.rhombusMapFromJsonMap(
				TestHelpers.getTestObject(2),
				definition.getDefinitions().get("testtype"));
		UUID key3 = om.update("testtype", key2, testObject2);

		//Get the updated object back and make sure it matches
		Map<String, Object> dbObject2 = om.getByKey("testtype", key3);
		for(String dbKey : dbObject2.keySet()) {
			//Verify that everything but the key is the same
			if(!dbKey.equals("id")) {
				assertEquals(testObject2.get(dbKey), dbObject2.get(dbKey));
			}
		}

		//Get from the original index
		dbObjects = om.list("testtype", criteria);
		assertEquals(0, dbObjects.size());

		//Get from the new index
		Criteria criteria2 = TestHelpers.getTestCriteria(1);
		criteria2.getIndexKeys().put("foreignid",((Integer)criteria2.getIndexKeys().get("foreignid")).longValue());
		dbObjects = om.list("testtype", criteria2);
		assertEquals(1, dbObjects.size());

		//Teardown connections
		cm.teardown();
	}

	//This does not test blob or counter types
	@Test
	public void testObjectTypes() throws Exception {
		logger.debug("Starting testObjectTypes");

		//Build the connection manager
		ConnectionManager cm = getConnectionManager();

		//Build our keyspace definition object
		CKeyspaceDefinition definition = JsonUtil.objectFromJsonResource(CKeyspaceDefinition.class, this.getClass().getClassLoader(), "ObjectMapperTypeTestKeyspace.js");
		assertNotNull(definition);

		//Rebuild the keyspace and get the object mapper
		cm.buildKeyspace(definition, true);
		cm.setDefaultKeyspace(definition);
		ObjectMapper om = cm.getObjectMapper();

		//Insert in some values of each type
		List<Map<String, Object>> values = JsonUtil.rhombusMapFromResource(this.getClass().getClassLoader(), "ObjectMapperTypeTestData.js");
		Map<String, Object> data = JsonUtil.rhombusMapFromJsonMap(values.get(0), definition.getDefinitions().get("testobjecttype"));
		UUID uuid = om.insert("testobjecttype", data);
		assertNotNull(uuid);

		//Get back the values
		Map<String, Object> returnedValues = om.getByKey("testobjecttype", uuid);

		//Verify that id is returned
		assertNotNull(returnedValues.get("id"));

		logger.debug("Returned values: {}", returnedValues);
		for(String returnedKey : returnedValues.keySet()) {
			if(!returnedKey.equals("id")) {
				Object insertValue = data.get(returnedKey);
				Object returnValue = returnedValues.get(returnedKey);
				assertEquals(insertValue, returnValue);
			}
		}
	}


	@Test
	public void testDateRangeQueries() throws Exception {
		logger.debug("Starting testDateRangeQueries");

		//Build the connection manager
		ConnectionManager cm = getConnectionManager();

		//Build our keyspace definition object
		CKeyspaceDefinition definition = JsonUtil.objectFromJsonResource(CKeyspaceDefinition.class, this.getClass().getClassLoader(), "AuditKeyspace.js");
		assertNotNull(definition);

		//Rebuild the keyspace and get the object mapper
		cm.buildKeyspace(definition, true);
		logger.debug("Built keyspace: {}", definition.getName());
		cm.setDefaultKeyspace(definition);
		ObjectMapper om = cm.getObjectMapper();

		//Insert our test data
		List<Map<String, Object>> values = JsonUtil.rhombusMapFromResource(this.getClass().getClassLoader(), "DateRangeQueryTestData.js");
		for(Map<String, Object> object : values) {
			Long createdAt = (Long)(object.get("created_at"));
			logger.debug("Inserting audit with created_at: {}", createdAt);
			om.insert("object_audit", JsonUtil.rhombusMapFromJsonMap(object,definition.getDefinitions().get("object_audit")), createdAt);
		}

		//Make sure that we have the proper number of results
		SortedMap<String, Object> indexValues = Maps.newTreeMap();
		indexValues.put("account_id", UUID.fromString("00000003-0000-0030-0040-000000030000"));
		indexValues.put("object_type", "Account");
		indexValues.put("object_id", UUID.fromString("00000003-0000-0030-0040-000000030000"));
		Criteria criteria = new Criteria();
		criteria.setIndexKeys(indexValues);
		criteria.setLimit(50L);
		List<Map<String, Object>> results = om.list("object_audit", criteria);
		assertEquals(8, results.size());

		//Now query for results since May 1 2013
		criteria.setStartTimestamp(1367366400000L);
		results = om.list("object_audit", criteria);
		assertEquals(7, results.size());

		//And for results since May 14, 2013
		criteria.setStartTimestamp(1368489600000L);
		results = om.list("object_audit", criteria);
		assertEquals(5, results.size());
	}

	private ConnectionManager getConnectionManager() throws IOException {
		//Get a connection manager based on the test properties
		ConnectionManager cm = new ConnectionManager(TestHelpers.getTestCassandraConfiguration());
		cm.setLogCql(true);
		cm.buildCluster();
		assertNotNull(cm);
		return cm;
	}
}
