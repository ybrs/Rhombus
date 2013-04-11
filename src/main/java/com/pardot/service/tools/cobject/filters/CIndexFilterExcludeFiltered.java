package com.pardot.service.tools.cobject.filters;

import java.util.HashMap;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/6/13
 */
public class CIndexFilterExcludeFiltered implements CIndexFilter{

	public boolean isIncluded(HashMap<String,String> obj){
		return obj.get("filtered").equals("1") ? false : true;
	}
}
