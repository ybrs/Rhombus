package com.pardot.rhombus.util;

import java.util.List;
import java.util.Map;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 5/14/13
 */
public class MapContainer {
	private List<Map<String, Object>> values;

	public MapContainer() {

	}

	public MapContainer(List<Map<String, Object>> values) {
		this.values = values;
	}

	public List<Map<String, Object>> getValues() {
		return values;
	}

	public void setValues(List<Map<String, Object>> values) {
		this.values = values;
	}
}