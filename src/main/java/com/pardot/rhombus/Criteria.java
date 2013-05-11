package com.pardot.rhombus;

import com.google.common.base.Objects;
import com.pardot.rhombus.cobject.CObjectOrdering;

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

	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("indexKeys", indexKeys.toString())
				.add("ordering", ordering.toString())
				.add("startTimestamp", startTimestamp.toString())
				.add("endTimestamp", endTimestamp.toString())
				.add("limit", limit.toString())
				.toString();
	}

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

	public void setStartTimestamp(Long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(Long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}
}
