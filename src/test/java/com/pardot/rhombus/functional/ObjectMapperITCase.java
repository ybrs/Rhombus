package com.pardot.rhombus.functional;


import com.pardot.rhombus.ConnectionManager;
import com.pardot.rhombus.Criteria;
import com.pardot.rhombus.ObjectMapper;
import com.pardot.rhombus.helpers.TestHelpers;
import com.pardot.rhombus.cobject.CKeyspaceDefinition;
import com.pardot.rhombus.cobject.CQLGenerationException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class ObjectMapperITCase {

	private static Logger logger = LoggerFactory.getLogger(ObjectMapperITCase.class);

	@Test
	public void testObjectMapper() throws IOException, CQLGenerationException, InterruptedException {
		//Get a connection manager based on the test properties
		ConnectionManager cm = new ConnectionManager(TestHelpers.getTestCassandraConfiguration());
		cm.buildCluster();
		assertNotNull(cm);

		//Build our keyspace definition object
		String json = TestHelpers.readFileToString(this.getClass(), "CKeyspaceTestData.js");
		CKeyspaceDefinition definition = CKeyspaceDefinition.fromJsonString(json);
		assertNotNull(definition);

		//Rebuild the keyspace and get the object mapper
		cm.rebuildKeyspace(definition);
		ObjectMapper om = cm.getObjectMapper();

		//Get a test object to insert
		Map<String, String> testObject = TestHelpers.getTestObject(0);
		UUID key = om.insert("testtype", testObject);

		//Query to get back the object from the database
		Map<String, String> dbObject = om.getByKey("testtype", key);
		assertEquals(testObject, dbObject);

		//Add another object with the same foreign key
		UUID key2 = om.insert("testtype", TestHelpers.getTestObject(1));

		//Query by foreign key
		Criteria criteria = TestHelpers.getTestCriteria(0);
		List<Map<String, String>> dbObjects = om.list("testtype", criteria);
		assertEquals(2, dbObjects.size());

		//Remove one of the objects we added
		om.delete("testtype", key);

		//Re-query by foreign key
		dbObjects = om.list("testtype", criteria);
		assertEquals(1, dbObjects.size());

		//Update the values of one of the objects
		Map<String, String> testObject2 = TestHelpers.getTestObject(2);
		UUID key3 = om.update("testtype", key2, testObject2);

		//Get the updated object back and make sure it matches
		Map<String, String> dbObject2 = om.getByKey("testtype", key3);
		assertEquals(testObject2, dbObject2);

		//Get from the original index
		dbObjects = om.list("testtype", criteria);
		assertEquals(0, dbObjects.size());

		//Get from the new index
		Criteria criteria2 = TestHelpers.getTestCriteria(1);
		dbObjects = om.list("testtype", criteria2);
		assertEquals(1, dbObjects.size());

		//Teardown connections
		cm.teardown();
	}
}
