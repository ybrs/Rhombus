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

	private CIndex index;
	private List<SortedMap<String,Object>> indexValues;
	private IndexUpdateRowKey rowKey;
	private Long timeStampOfMostCurrentUpdate;

	public IndexUpdateRow(String objectName, UUID instanceId, CIndex index, Long timeStampOfMostCurrentUpdate, List<SortedMap<String,Object>> indexValues){
	    this.rowKey = new IndexUpdateRowKey(objectName, instanceId);
		this.index = index;
		this.indexValues = indexValues;
		this.timeStampOfMostCurrentUpdate = timeStampOfMostCurrentUpdate;
	}

	public String getObjectName() {
		return this.rowKey.getObjectName();
	}


	public CIndex getIndex() {
		return index;
	}

	public void setIndex(CIndex index) {
		this.index = index;
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

}
