package com.pardot.service.tools.cobject;

import com.pardot.service.tools.cobject.filters.CIndexFilter;

import java.util.ArrayList;
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
	private ArrayList<String> keylist;
	public ArrayList<CIndexFilter> filters;

	public String createIndexKey(HashMap obj){
		//loop through the keylist and contruct the actual index key
		return "test:test:test";
	}

}
