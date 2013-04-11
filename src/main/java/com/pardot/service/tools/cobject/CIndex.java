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

	public String name;
	public String key;
	public List<String> compositeKeyList;
	public List<CIndexFilter> filters;

	public CIndex(String name, String key){
		this.name = name;
		this.key = key;
		this.compositeKeyList = new ArrayList<String>(Arrays.asList(key.split("\\s*:\\s*")));
	}

	public boolean passesAllFilters(Map<String,String> data){
		for(CIndexFilter f : this.filters){
			if(!f.isIncluded(data)){
				return false;
			}
		}
		return true;
	}

}
