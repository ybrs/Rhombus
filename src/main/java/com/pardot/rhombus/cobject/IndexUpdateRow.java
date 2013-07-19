package com.pardot.rhombus.cobject;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 7/9/13
 */
public class IndexUpdateRow {

	private List<SortedMap<String,Object>> indexValues;
	private IndexUpdateRowKey rowKey;
	private Long timeStampOfMostCurrentUpdate;
	private List<UUID> ids;

	public IndexUpdateRow(String objectName, UUID instanceId, Long timeStampOfMostCurrentUpdate, List<SortedMap<String,Object>> indexValues, List<UUID> ids){
		this.rowKey = new IndexUpdateRowKey(objectName, instanceId);
		this.indexValues = indexValues;
		this.timeStampOfMostCurrentUpdate = timeStampOfMostCurrentUpdate;
		this.ids = ids;
	}

	public String getObjectName() {
		return this.rowKey.getObjectName();
	}

	public List<SortedMap<String, Object>> getIndexValues() {
		return indexValues;
	}

	public void setIndexValues(List<SortedMap<String, Object>> indexValues) {
		this.indexValues = indexValues;
	}

	public IndexUpdateRowKey getRowKey() {
		return rowKey;
	}

	public void setRowKey(IndexUpdateRowKey rowKey) {
		this.rowKey = rowKey;
	}

	public UUID getInstanceId() {
		return this.rowKey.getInstanceId();
	}

	public Long getTimeStampOfMostCurrentUpdate() {
		return timeStampOfMostCurrentUpdate;
	}

	public void setTimeStampOfMostCurrentUpdate(Long timeStampOfMostCurrentUpdate) {
		this.timeStampOfMostCurrentUpdate = timeStampOfMostCurrentUpdate;
	}

	public List<UUID> getIds() {
		return ids;
	}

	public void setIds(List<UUID> ids) {
		this.ids = ids;
	}

}
