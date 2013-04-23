package com.pardot.rhombus.helpers;

import com.google.common.collect.Lists;
import com.pardot.rhombus.CassandraConfiguration;
import com.pardot.rhombus.Criteria;
import com.pardot.rhombus.util.JsonUtil;
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
		return JsonUtil.objectFromJsonResource(CassandraConfiguration.class, CassandraConfiguration.class.getClassLoader(), "cassandra-functional.js");
	}

	public static Map<String, String> getTestObject(int index) {
		if(testObjects == null) {
			try {
				testObjects = (List<Map<String, String>>)JsonUtil.objectFromJsonResource(List.class, TestHelpers.class.getClassLoader(), "TestObjects.js");
			} catch (IOException e) {
				testObjects = Lists.newArrayList();
			}
		}
		return testObjects.get(index);
	}

	public static Criteria getTestCriteria(int index) {
		if(criteriaHolder == null) {
			try {
				criteriaHolder = JsonUtil.objectFromJsonResource(CriteriaHolder.class, TestHelpers.class.getClassLoader(), "TestCriteria.js");
			} catch (IOException e) {
				criteriaHolder = new CriteriaHolder();
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
