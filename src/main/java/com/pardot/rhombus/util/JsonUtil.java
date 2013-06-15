package com.pardot.rhombus.util;

import com.datastax.driver.core.Row;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.pardot.rhombus.cobject.CDefinition;
import com.pardot.rhombus.cobject.CField;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

	public static List<Map<String, Object>> rhombusMapFromResource(ClassLoader resourceClassLoader, String resourceLocation) throws IOException {
		ObjectMapper om = new ObjectMapper();
		InputStream inputStream = resourceClassLoader.getResourceAsStream(resourceLocation);
		MapContainer mc = om.readValue(inputStream, MapContainer.class);
		inputStream.close();
		return mc.getValues();
	}

	public static Map<String, Object> rhombusMapFromJsonMap(Map<String, Object> jsonMap, CDefinition definition) {
		Map<String, Object> rhombusMap = Maps.newHashMap();
		for(CField field : definition.getFields().values()) {
			Object jsonValue = jsonMap.get(field.getName());
			if(jsonValue != null) {
				rhombusMap.put(field.getName(), typedObjectFromValueAndField(jsonValue, field));
			}
		}

		return rhombusMap;
	}

	private static Object typedObjectFromValueAndField(Object jsonValue, CField field) {
		if(jsonValue == null) {
			return null;
		}
		Object fieldValue;
		switch(field.getType()) {
			case ASCII:
			case VARCHAR:
			case TEXT:
				Preconditions.checkArgument(String.class.isAssignableFrom(jsonValue.getClass()));
				return jsonValue;
			case BIGINT:
			case COUNTER:
				return longFromNumber(jsonValue);
			case BLOB:
				throw new IllegalArgumentException();
			case BOOLEAN:
				if(jsonValue instanceof Boolean){
					return jsonValue;
				}else {
					throw new IllegalArgumentException();
				}
			case DECIMAL:
				if(jsonValue instanceof Float){
					return BigDecimal.valueOf(((Float)jsonValue).floatValue());
				} else if(jsonValue instanceof Double) {
					return BigDecimal.valueOf(((Double)jsonValue).doubleValue());
				}else {
					throw new IllegalArgumentException();
				}
			case DOUBLE:
				if(jsonValue instanceof Double){
					return Double.valueOf(((Float)jsonValue).floatValue());
				} else {
					throw new IllegalArgumentException();
				}
			case FLOAT:
				if(jsonValue instanceof Double){
					return Float.valueOf(((Float)jsonValue).floatValue());
				} else {
					throw new IllegalArgumentException();
				}
			case INT:
				return intFromNumber(jsonValue);
			case TIMESTAMP:
				if(jsonValue instanceof Integer){
					return new Date(((Integer)jsonValue).longValue());
				} else if(jsonValue instanceof Long) {
					return new Date(((Long)jsonValue).longValue());
				} else {
					throw new IllegalArgumentException("Wrong type for "+ jsonValue + " ("+jsonValue.getClass()+")");
				}
			case UUID:
			case TIMEUUID:
				if(jsonValue instanceof String){
					return UUID.fromString((String)jsonValue);
				} else {
					throw new IllegalArgumentException();
				}
			case VARINT:
				if(jsonValue instanceof String){
					return BigInteger.valueOf(Long.parseLong((String)jsonValue));
				} else {
					throw new IllegalArgumentException();
				}
			default:
				return null;
		}
	}

	private static Long longFromNumber(Object number) {
		if(Boolean.class.isAssignableFrom(number.getClass())) {
			return ((Boolean)number ? 1L : 0L);
		} else if(Integer.class.isAssignableFrom(number.getClass())) {
			return ((Integer)number).longValue();
		} else if(Long.class.isAssignableFrom(number.getClass())) {
			return (Long)number;
		} else {
			throw new IllegalArgumentException();
		}
	}

	private static Integer intFromNumber(Object number) {
		if(Boolean.class.isAssignableFrom(number.getClass())) {
			return ((Boolean)number ? 1 : 0);
		} else if(Integer.class.isAssignableFrom(number.getClass())) {
			return ((Integer)number).intValue();
		} else if(Integer.class.isAssignableFrom(number.getClass())) {
			return (Integer)number;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
