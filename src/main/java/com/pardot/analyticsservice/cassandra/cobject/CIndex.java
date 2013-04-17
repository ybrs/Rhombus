package com.pardot.analyticsservice.cassandra.cobject;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pardot.analyticsservice.cassandra.cobject.filters.CIndexFilter;
import com.pardot.analyticsservice.cassandra.cobject.shardingstrategy.TimebasedShardingStrategy;

import java.util.*;

/**
 * Pardot, An ExactTarget Company.
 * User: robrighter
 * Date: 4/4/13
 */
public class CIndex {

	private String name;
	private String key;
	private List<String> compositeKeyList;
	private List<CIndexFilter> filters;
	private TimebasedShardingStrategy shardingStrategy;

	public CIndex() {

	}

	public CIndex(String name, String key, TimebasedShardingStrategy shardingStrategy){
		this.name = name;
		this.setKey(key);
		this.shardingStrategy = shardingStrategy;
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

	public TimebasedShardingStrategy getShardingStrategy(){
		return shardingStrategy;
	}

	public void setShardingStrategy(TimebasedShardingStrategy shardingStrategy){
		this.shardingStrategy = shardingStrategy;
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
		ArrayList<String> listtoset = new ArrayList<String>(Arrays.asList(key.split("\\s*:\\s*")));
		java.util.Collections.sort(listtoset);
		this.compositeKeyList = listtoset;
		this.key = Joiner.on(":").join(listtoset);
	}

	public List<String> getCompositeKeyList() {
		return compositeKeyList;
	}

	public List<CIndexFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<CIndexFilter> filters) {
		this.filters = filters;
	}

	public List<String> getIndexValues(Map<String,String> allValues){
		List<String> ret = Lists.newArrayList();
		for(String key : compositeKeyList){
			ret.add(allValues.get(key));
		}
		return ret;
	}

}
