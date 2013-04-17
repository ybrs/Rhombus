package com.pardot.analyticsservice.cassandra.cobject.filters;

import java.util.Map;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/6/13
 */
public class CIndexFilterIncludeAll extends CIndexFilter {

	public boolean isIncluded(Map<String,String> obj){
		return true;
	}

}
