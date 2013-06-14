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
import java.util.Date;
import java.util.List;
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
				fieldValue = jsonValue;
				break;
			case BIGINT:
			case COUNTER:
				fieldValue = longFromNumber(jsonValue);
				break;
			case BLOB:
				throw new IllegalArgumentException();
			case BOOLEAN:
				fieldValue = booleanFromNumber(jsonValue);
				break;
			case DECIMAL:
				fieldValue = jsonValue.getDecimal(field.getName());
				break;
			case DOUBLE:
				fieldValue = jsonValue.getDouble(field.getName());
				break;
			case FLOAT:
				fieldValue = jsonValue.getFloat(field.getName());
				break;
			case INT:
				fieldValue = jsonValue.getInt(field.getName());
				break;
			case TIMESTAMP:
				fieldValue = jsonValue.getDate(field.getName());
				if(fieldValue != null) {
					fieldValue = ((Date)fieldValue).getTime();
				}
				break;
			case UUID:
			case TIMEUUID:
				fieldValue = jsonValue.getUUID(field.getName());
				break;
			case VARINT:
				fieldValue = jsonValue.getVarint(field.getName());
				break;
			default:
				fieldValue = null;
		}
		return (fieldValue == null ? null : fieldValue.toString());
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

}
