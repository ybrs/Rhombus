package com.pardot.service.analytics.helpers;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

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

}
