package com.pardot.service.tools.cobject;

import com.pardot.service.tools.cobject.filters.CIndexFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

/**
 * Pardot, An ExactTarget Company.
 * User: robrighter
 * Date: 4/4/13
 */
public class CIndex {

	private String name;
	private String key;
	public List<String> compositeKeyList;
	public List<CIndexFilter> filters;

	public CIndex() {

	}

	public CIndex(String name, String key){
		this.name = name;
		this.setKey(key);
	}

	public boolean passesAllFilters(Map<String,String> data){
		for(CIndexFilter f : this.filters){
			if(!f.isIncluded(data)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine if the keys provided can be constructed
	 * to form a composite key for this index. In other words
	 * this method answers the question "Can I query this index
	 * using this criteria"
	 * @return boolean - true if it is queryable
	 */
	public boolean validateIndexKeys(Map<String,String> keys){
		if(keys.size() != compositeKeyList.size()){
			//optimized return if we have a size mismatch
			return false;
		}
		for(String s : compositeKeyList){
			if(keys.get(s) == null){
				return false;
			}
		}
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		this.compositeKeyList = new ArrayList<String>(Arrays.asList(key.split("\\s*:\\s*")));
	}
}
