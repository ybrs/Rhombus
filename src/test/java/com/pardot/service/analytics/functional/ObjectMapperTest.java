package com.pardot.service.analytics.functional;


import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.pardot.cassandra.ConnectionManager;
import com.pardot.cassandra.ObjectMapper;
import com.pardot.service.analytics.helpers.TestHelpers;
import com.pardot.service.tools.cobject.CKeyspaceDefinition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static junit.framework.TestCase.*;

public class ObjectMapperTest {

	private static Logger logger = LoggerFactory.getLogger(ObjectMapperTest.class);

	@Test
	public void testKeyspaceBuild() throws IOException {
		//Get a connection manager based on the test properties
		ConnectionManager cm = new ConnectionManager(TestHelpers.getTestProperties());
		assertNotNull(cm);

		//Build our keyspace definition object
		String json = TestHelpers.readFileToString(this.getClass(), "CKeyspaceTestData.js");
		CKeyspaceDefinition definition = CKeyspaceDefinition.fromJsonString(json);
		assertNotNull(definition);

		//Rebuild the keyspace and get the object mapper
		cm.rebuildKeyspace(definition);
		ObjectMapper om = cm.getObjectMapper();

		Map<String, String> testObject = TestHelpers.getTestObject(0);
		logger.debug(testObject.toString());

		//Teardown connections
		cm.teardown();
	}
}
