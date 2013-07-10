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

	private String objectName;
	private UUID instanceId;
	private CIndex index;
	private List<SortedMap<String,Object>> indexValues;
	private Long rowKey;
	private Long timeStampOfMostCurrentUpdate;

	public IndexUpdateRow(String objectName, UUID instanceId, Long rowKey, CIndex index, Long timeStampOfMostCurrentUpdate, List<SortedMap<String,Object>> indexValues){
	    this.objectName = objectName;
		this.rowKey = rowKey;
		this.index = index;
		this.instanceId = instanceId;
		this.indexValues = indexValues;
		this.timeStampOfMostCurrentUpdate = timeStampOfMostCurrentUpdate;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
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

	public Long getRowKey() {
		return rowKey;
	}

	public void setRowKey(Long rowKey) {
		this.rowKey = rowKey;
	}

	public UUID getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(UUID instanceId) {
		this.instanceId = instanceId;
	}

	public Long getTimeStampOfMostCurrentUpdate() {
		return timeStampOfMostCurrentUpdate;
	}

	public void setTimeStampOfMostCurrentUpdate(Long timeStampOfMostCurrentUpdate) {
		this.timeStampOfMostCurrentUpdate = timeStampOfMostCurrentUpdate;
	}

}
