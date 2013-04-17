package com.pardot.analyticsservice.helpers;
import com.pardot.analyticsservice.cassandra.Criteria;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

	public static Properties getTestProperties() throws IOException {
		String filename = "cassandra.properties";
		InputStream inputStream = TestHelpers.class.getClassLoader().getResourceAsStream(filename);
		Properties properties = new Properties();
		properties.load(inputStream);
		inputStream.close();
		return properties;
	}

	public static Map<String, String> getTestObject(int index) {
		if(testObjects == null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				String json = TestHelpers.readFileToString(TestHelpers.class, "TestObjects.js");
				List root = mapper.readValue(json, List.class);
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
				String json = TestHelpers.readFileToString(TestHelpers.class, "TestCriteria.js");
				criteriaHolder = mapper.readValue(json, CriteriaHolder.class);
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
