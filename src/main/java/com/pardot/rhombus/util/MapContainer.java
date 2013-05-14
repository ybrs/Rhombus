package com.pardot.rhombus.util;

import java.util.Map;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 5/14/13
 */
public class MapContainer {
	private Map<String, String> values;

	public MapContainer() {

	}

	public MapContainer(Map<String, String> values) {
		this.values = values;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}
}