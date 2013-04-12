package com.pardot.service.analytics.functional;

import static org.junit.Assert.*;
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
		ConnectionManager cm = new ConnectionManager(getTestProperties());
		Session session = cm.getSession();
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

	private Properties getTestProperties() throws IOException {
		String filename = "cassandra.properties";
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filename);
		Properties properties = new Properties();
		properties.load(inputStream);
		inputStream.close();
		return properties;
	}
}
