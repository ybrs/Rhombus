package com.pardot.service.analytics.functional;

import com.pardot.cassandra.ConnectionManager;
import com.pardot.service.analytics.helpers.TestHelpers;
import com.pardot.service.tools.cobject.CDefinition;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: michaelfrank
 * Date: 4/11/13
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObjectMapperTest {

	@Test
	public void testKeyspaceCreate() throws IOException {
		//Create a connection manager for the test environment
		ConnectionManager cm = new ConnectionManager(TestHelpers.getTestProperties());

		//Create an AnalyticsService object
		AnalyticsService service = new AnalyticsService(cm);

		//First make sure that the test keyspace is removed;
		service.removeKeyspace("functional");

		//Build our keyspace from the test configuration
		String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
		CDefinition def = CDefinition.fromJsonString(json);
		service.buildKeyspaceFromDefinition("functional", def);

		//Finally tear down all connections managed by the service
		service.teardown();
	}

}
