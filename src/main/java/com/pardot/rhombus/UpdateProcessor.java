package com.pardot.rhombus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;
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
		if(row.getIndexValues().size() == 0){
			return;
		}
		if(row.getIndexValues().size() == 1){
			//todo: if this is older than 2 times the consistency horizon, just delete it
			return;
		}

		//make a list of all the updated indexes and subtract all the indexes that are current
		Map<String,Object> mostRecentUpdate = row.getIndexValues().get(0);
		row.getIndexValues().remove(0);
		row.getIds().remove(0);
		List<Map<String,Object>> listToDelete = Lists.newArrayList();
		for(Map<String,Object> update: row.getIndexValues()){
			if(!areIndexValuesEqual(mostRecentUpdate, update)){
				listToDelete.add(update);
			}
		}
		//delete the list of indexes with a timestamp of the current update
		for(Map<String,Object> iv : listToDelete){
			objectMapper.deleteObsoleteIndex(row,iv);
		}

		//now delete the processed update columns in this row
		for(UUID todelete: row.getIds()){
			objectMapper.deleteObsoleteUpdateIndexColumn(row.getRowKey(),todelete);
		}

	}

	protected boolean areIndexValuesEqual(Map<String,Object> a, Map<String,Object> b){
		if(a.keySet().size() != b.keySet().size()){
			return false;
		}
		for(String key: a.keySet()){
			if(a.get(key) != b.get(key)){
				return false;
			}
		}
		return true;
	}


}
