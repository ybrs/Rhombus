package com.pardot.service.analytics.helpers;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/5/13
 */
public class TestHelpers {

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
}
