package com.pardot.service.analytics.functional;


import com.pardot.cassandra.ConnectionManager;
import com.pardot.service.analytics.helpers.TestHelpers;
import com.pardot.service.tools.cobject.CKeyspaceDefinition;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.*;

public class ObjectMapperTest {

	@Test
	public void testKeyspaceDropAndCreate() throws IOException {
		//Get a connection manager based on the test properties
		ConnectionManager cm = new ConnectionManager(TestHelpers.getTestProperties());
		assertNotNull(cm);

		//Build our keyspace definition object
		String json = TestHelpers.readFileToString(this.getClass(), "CKeyspaceTestData.js");
		CKeyspaceDefinition definition = CKeyspaceDefinition.fromJsonString(json);
		assertNotNull(definition);

		//Rebuild the keyspace
		cm.rebuildKeyspace(definition);
	}


	@Test
	public void testKeyspaceBuild() throws IOException {


	}

}
