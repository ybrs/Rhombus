package com.pardot.rhombus.cobject;

import com.datastax.driver.core.Row;

import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 7/15/13
 */
public class IndexUpdateRowKey {

	private UUID instanceId;
	private String objectName;

	public IndexUpdateRowKey(String objectName, UUID instanceId){
		this.objectName = objectName;
		this.instanceId = instanceId;
	}

	public IndexUpdateRowKey(Row row){
		this.objectName = row.getString("statictablename");
		this.instanceId = row.getUUID("instanceid");
	}

	public UUID getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(UUID instanceId) {
		this.instanceId = instanceId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

}
