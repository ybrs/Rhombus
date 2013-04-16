package com.pardot.service.analytics.functional;

import static org.junit.Assert.*;

import com.pardot.service.analytics.helpers.TestHelpers;
import org.junit.Test;

import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.Session;

import com.pardot.cassandra.ConnectionManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: michaelfrank
 * Date: 4/11/13
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class CassandraConnectionTest {

	@Test
	public void testKeyspaceCreate() throws IOException {
		ConnectionManager cm = new ConnectionManager(TestHelpers.getTestProperties());
		Session session = cm.getEmptySession();
		assertNotNull(session);

		//Create the functional keyspace
		try {
			session.execute("CREATE KEYSPACE functional WITH replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 }");
		} catch (AlreadyExistsException e) {
		 	//Ignore
		}

		//Change to our functional testing keyspace
		session.execute("USE functional");

		//Shutdown the session
		session.shutdown();
	}


}
