package com.pardot.rhombus.cobject;

import java.util.Iterator;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public interface CQLStatementIterator extends Iterator<CQLStatement>{

	public boolean hasNext(long currentResultCount);
	public boolean isBounded();
	public long size();
}
