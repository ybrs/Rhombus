package com.pardot.rhombus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pardot.rhombus.cobject.CObjectCQLGenerator;
import com.pardot.rhombus.cobject.CQLStatement;
import com.pardot.rhombus.cobject.IndexUpdateRow;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 7/5/13
 */
public class UpdateProcessor {

	private ObjectMapper objectMapper;

	public UpdateProcessor(ObjectMapper om){
		this.objectMapper = om;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void process() throws IOException {
		IndexUpdateRow row = objectMapper.getNextUpdateIndexRow(null);
		while(row != null){
			processRow(row);
			row = objectMapper.getNextUpdateIndexRow(row.getRowKey());
		}
	}

	protected void processRow(IndexUpdateRow row){
		//make a list of all the updated indexes

		//subtract all the indexes that are current

		//delete the list of indexes with a timestamp of the current update

	}


}
