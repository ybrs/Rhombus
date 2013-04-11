package com.pardot.service.tools.cobject.filters;

import java.util.Map;

/**
 * Pardot, An ExactTarget Company.
 * User: robrighter
 * Date: 4/4/13
 */
public interface CIndexFilter {

	public boolean isIncluded(Map<String,String> obj);
}
