package com.pardot.service.tools.cobject;

import java.util.Iterator;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public interface CQLStatementIterator extends Iterator<String>{

	public boolean hasNext(long currentResultCount);
	public boolean isBounded();
	public long size();
}
