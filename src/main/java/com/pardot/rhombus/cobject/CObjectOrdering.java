package com.pardot.rhombus.cobject;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/15/13
 */
public enum CObjectOrdering {
	ASCENDING("ASC"),
	DESCENDING("DESC");

	private String text;

	CObjectOrdering(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public String toString() {
		return this.text;
	}

	public static CObjectOrdering fromString(String text) {
		for(CObjectOrdering obj : CObjectOrdering.values()) {
			if(text.equalsIgnoreCase(obj.getText()))  {
				return obj;
			}
		}
		return null;
	}
}
