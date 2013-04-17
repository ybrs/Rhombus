package com.pardot.analyticsservice.cassandra.cobject.filters;

import java.util.Map;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/6/13
 */
public class CIndexFilterExcludeFiltered extends CIndexFilter{

	public boolean isIncluded(Map<String,String> obj){
		return obj.get("filtered").equals("1") ? false : true;
	}
}
