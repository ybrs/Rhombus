package com.pardot.service.analytics.helpers;

import junit.framework.Test;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/5/13
 */
public class TestHelpers {

	public static String readFileToString(Class testclass, String filename){
		String ret = "";
		try{

			FileInputStream inputStream;
			inputStream = new FileInputStream( getFullResourceLocation(testclass, filename) );
			ret = IOUtils.toString(inputStream);
			inputStream.close();
		}
		catch (Exception e){
			//do nothing
			System.out.println(e.toString());
		}
		return ret;
	}

	public static String getFullResourceLocation(Class testclass, String filename){
		java.net.URL location = testclass.getProtectionDomain().getCodeSource().getLocation();
		System.out.println("The location is: ");
		String ret = location.getFile()+"/com/pardot/service/analytics/resources/"+filename;
		System.out.println(ret);
		return ret;
	}

}
