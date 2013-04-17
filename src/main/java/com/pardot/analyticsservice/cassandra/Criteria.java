package com.pardot.analyticsservice.cassandra;

import com.pardot.service.tools.cobject.CObjectOrdering;

import java.util.Map;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class Criteria {

	private String index;
	private Map<String, String> indexKeys;
	private CObjectOrdering ordering;
	private Long startTimestamp;
	private Long endTimestamp;
	private Long limit;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Map<String, String> getIndexKeys() {
		return indexKeys;
	}

	public void setIndexKeys(Map<String, String> indexKeys) {
		this.indexKeys = indexKeys;
	}

	public CObjectOrdering getOrdering() {
		return ordering;
	}

	public void setOrdering(String ordering) {
		this.ordering = CObjectOrdering.fromString(ordering);
	}

	public Long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}
}
