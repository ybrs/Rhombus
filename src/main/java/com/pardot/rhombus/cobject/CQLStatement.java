package com.pardot.rhombus.cobject;

import com.datastax.driver.core.BoundStatement;

import java.sql.PreparedStatement;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 6/10/13
 */
public class CQLStatement {
	private String query;
	private Object[] values;
	private boolean isPreparable;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public boolean isPreparable() {
		return isPreparable;
	}

	public void setPreparable(boolean preparable) {
		isPreparable = preparable;
	}
}
