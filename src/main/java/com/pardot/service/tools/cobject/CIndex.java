package com.pardot.service.tools.cobject;

import com.pardot.service.tools.cobject.filters.CIndexFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * Pardot, An ExactTarget Company.
 * User: robrighter
 * Date: 4/4/13
 */
public class CIndex {

	public String name;
	public String key;
	public ArrayList<String> compositeKeyList;
	public ArrayList<CIndexFilter> filters;

	public CIndex(String name, String key){
		this.name = name;
		this.key = key;
		this.compositeKeyList = new ArrayList<String>(Arrays.asList(key.split("\\s*:\\s*")));
	}

	public String createIndexKey(HashMap obj){
		//loop through the keylist and contruct the actual index key
		return "test:test:test";
	}

}
