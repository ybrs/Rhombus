package com.pardot.rhombus.cobject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.*;

/**
 * Pardot, An ExactTarget Company.
 * User: robrighter
 * Date: 4/4/13
 */
public class CDefinition {

	private String name;
	private Map<String, CField> fields;
	private Map<String, CIndex> indexes;
	private SortedMap<String, CIndex> indexesIndexedByFields;
	private boolean allowNullPrimaryKeyInserts = false;

	public CDefinition(){
	}

	public static CDefinition fromJsonString(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, CDefinition.class);
	}

	//Getters and setters for Jackson
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, CField> getFields() {
		return fields;
	}
	public void setFields(List<CField> fields) {
		this.fields = Maps.newHashMap();
		for(CField field : fields) {
			this.fields.put(field.getName(), field);
		}
	}
	public Map<String, CIndex> getIndexes() {
		return indexes;
	}
	public void setIndexes(List<CIndex> indexes) {
		this.indexes = Maps.newHashMap();
		this.indexesIndexedByFields = Maps.newTreeMap();
		for(CIndex index : indexes) {
			this.indexes.put(index.getName(), index);
			this.indexesIndexedByFields.put(index.getKey(),index);
		}
	}

	public boolean isAllowNullPrimaryKeyInserts() {
		return allowNullPrimaryKeyInserts;
	}

	public void setAllowNullPrimaryKeyInserts(boolean allowNullPrimaryKeyInserts) {
		this.allowNullPrimaryKeyInserts = allowNullPrimaryKeyInserts;
	}

	public Collection<String> getRequiredFields(){
		Map<String,String> ret = Maps.newHashMap();
		for( CIndex i : indexes.values()){
			for(String key : i.getCompositeKeyList()){
				ret.put(key,key);
			}
		}
		return ret.values();
	}

	public CIndex getIndex(SortedMap<String,String> indexValues){
		String key = Joiner.on(":").join(indexValues.keySet());
		return indexesIndexedByFields.get(key);
	}

}
