package com.pardot.service.tools.cobject;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: michaelfrank
 * Date: 4/15/13
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class CKeyspaceDefinition {
	private String name;
	private Collection<CDefinition> definitions;

	public CKeyspaceDefinition() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<CDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(Collection<CDefinition> definitions) {
		this.definitions = definitions;
	}
}
