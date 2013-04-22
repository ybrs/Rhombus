package com.pardot.rhombus.cobject;

import java.util.List;
import java.util.Iterator;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/15/13
 */
public class BoundedCQLStatementIterator implements CQLStatementIterator {

	private long size = 0;
	private Iterator<String> statementIterator;

	public BoundedCQLStatementIterator(List<String> CQLStatements){
		this.size = (long)CQLStatements.size();
		this.statementIterator = CQLStatements.iterator();
	}

	@Override
	public boolean hasNext() {
		return statementIterator.hasNext();
	}

	public boolean hasNext(long currentResultCount){
		return this.hasNext();
	}

	@Override
	public String next() {
		return statementIterator.next();
	}

	public boolean isBounded(){
		return true;
	}

	public long size(){
		return size;
	}

	@Override
	public void remove() {
		//To change body of implemented methods use File | Settings | File Templates.
	}


}
