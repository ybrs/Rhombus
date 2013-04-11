package com.pardot.service.tools.cobject.filters;

import java.util.Map;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/6/13
 */
public class CIndexFilterIncludeAll implements CIndexFilter {

	public boolean isIncluded(Map<String,String> obj){
		return true;
	}

}
