package com.pardot.analyticsservice.helpers;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.pardot.analyticsservice.cassandra.CassandraConfiguration;
import com.pardot.analyticsservice.cassandra.Criteria;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/5/13
 */
public class TestHelpers {

	private static List<Map<String, String>> testObjects;
	private static CriteriaHolder criteriaHolder;

	public static String readFileToString(Class testclass, String filename){
		String ret = "";
		try{
			InputStream inputStream;
			inputStream = testclass.getClassLoader().getResourceAsStream(filename);
			ret = IOUtils.toString(inputStream);
			inputStream.close();
		}
		catch (Exception e){
            e.printStackTrace();
		}
		return ret;
	}

	public static CassandraConfiguration getTestCassandraConfiguration() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String filename = "cassandra-functional.js";
		InputStream inputStream = TestHelpers.class.getClassLoader().getResourceAsStream(filename);
		CassandraConfiguration configuration = mapper.readValue(inputStream, CassandraConfiguration.class);
		inputStream.close();
		return configuration;
	}

	public static Map<String, String> getTestObject(int index) {
		if(testObjects == null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				InputStream inputStream = TestHelpers.class.getClassLoader().getResourceAsStream("TestObjects.js");
				List root = mapper.readValue(inputStream, List.class);
				inputStream.close();
				testObjects = (List<Map<String, String>>)root;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return testObjects.get(index);
	}

	public static Criteria getTestCriteria(int index) {
		if(criteriaHolder == null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				InputStream inputStream = TestHelpers.class.getClassLoader().getResourceAsStream("TestCriteria.js");
				criteriaHolder = mapper.readValue(inputStream, CriteriaHolder.class);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return criteriaHolder.getCriteria().get(index);
	}

	static class CriteriaHolder {
		private List<Criteria> criteria;

		List<Criteria> getCriteria() {
			return criteria;
		}

		void setCriteria(List<Criteria> criteria) {
			this.criteria = criteria;
		}
	}
}
