package com.pardot.rhombus.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.pardot.rhombus.cobject.CDefinition;
import com.pardot.rhombus.cobject.CField;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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

	public static SortedMap<String, Object> rhombusMapFromJsonMap(Map<String, Object> jsonMap, CDefinition definition) {
		SortedMap<String, Object> rhombusMap = Maps.newTreeMap();
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
		switch(field.getType()) {
			case ASCII:
			case VARCHAR:
			case TEXT:
				if(String.class.isAssignableFrom(jsonValue.getClass())) {
					return jsonValue;
				} else {
					throw new IllegalArgumentException();
				}
			case BIGINT:
			case COUNTER:
				return longFromNumber(jsonValue);
			case BLOB:
				throw new IllegalArgumentException();
			case BOOLEAN:
				if(Boolean.class.isAssignableFrom(jsonValue.getClass())){
					return jsonValue;
				}else {
					throw new IllegalArgumentException();
				}
			case DECIMAL:
				if(Float.class.isAssignableFrom(jsonValue.getClass())){
					return BigDecimal.valueOf((Float)jsonValue);
				} else if(Double.class.isAssignableFrom(jsonValue.getClass())) {
					return BigDecimal.valueOf((Double)jsonValue);
				}else {
					throw new IllegalArgumentException();
				}
			case DOUBLE:
				if(Double.class.isAssignableFrom(jsonValue.getClass())){
					return jsonValue;
				}
				else if(Float.class.isAssignableFrom(jsonValue.getClass())){
					return Double.valueOf((Float)jsonValue);
				}
				else {
					throw new IllegalArgumentException();
				}
			case FLOAT:
				if(Double.class.isAssignableFrom(jsonValue.getClass())){
					return Double.valueOf((Double)jsonValue).floatValue();
				}
				else if(Float.class.isAssignableFrom(jsonValue.getClass())){
					return jsonValue;
				} else {
					throw new IllegalArgumentException();
				}
			case INT:
				return intFromNumber(jsonValue);
			case TIMESTAMP:
				if(Date.class.isAssignableFrom(jsonValue.getClass())) {
					return jsonValue;
				} else if(Integer.class.isAssignableFrom(jsonValue.getClass())){
					return new Date(((Integer)jsonValue).longValue());
				} else if(Long.class.isAssignableFrom(jsonValue.getClass())) {
					return new Date((Long)jsonValue);
				} else {
					throw new IllegalArgumentException("Wrong type for "+ jsonValue + " ("+jsonValue.getClass()+")");
				}
			case UUID:
			case TIMEUUID:
				if(UUID.class.isAssignableFrom(jsonValue.getClass())) {
					return jsonValue;
				} else if(String.class.isAssignableFrom(jsonValue.getClass())){
					return UUID.fromString((String)jsonValue);
				} else {
					throw new IllegalArgumentException();
				}
			case VARINT:
				if(String.class.isAssignableFrom(jsonValue.getClass())) {
					return new BigInteger((String)jsonValue);
				}
				return BigInteger.valueOf(longFromNumber(jsonValue));
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
			return (Integer)number;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
