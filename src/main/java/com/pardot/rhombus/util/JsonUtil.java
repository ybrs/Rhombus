package com.pardot.rhombus.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/23/13
 */
public class JsonUtil {

	public static <T> T objectFromJsonResource(Class<T> objectClass, ClassLoader resourceClassLoader, String resourceLocation) throws IOException {
		ObjectMapper om = new ObjectMapper();
		InputStream inputStream = resourceClassLoader.getResourceAsStream(resourceLocation);
		T returnObject = om.readValue(inputStream, objectClass);
		inputStream.close();
		return returnObject;
	}

	public static Map<String, String> rhombusMapFromResource(ClassLoader resourceClassLoader, String resourceLocation) throws IOException {
		ObjectMapper om = new ObjectMapper();
		InputStream inputStream = resourceClassLoader.getResourceAsStream(resourceLocation);
		//System.out.println(inputStream.toString());
		MapContainer mc = om.readValue(inputStream, MapContainer.class);
		inputStream.close();
		return mc.getValues();
	}
}
