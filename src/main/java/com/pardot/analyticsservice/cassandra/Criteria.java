package com.pardot.analyticsservice.cassandra;

import com.pardot.analyticsservice.cassandra.cobject.CObjectOrdering;

import java.util.Map;
import java.util.SortedMap;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class Criteria {

	private SortedMap<String, String> indexKeys;
	private CObjectOrdering ordering;
	private Long startTimestamp;
	private Long endTimestamp;
	private Long limit;

	public SortedMap<String, String> getIndexKeys() {
		return indexKeys;
	}

	public void setIndexKeys(SortedMap<String, String> indexKeys) {
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
