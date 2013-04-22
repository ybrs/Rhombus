package com.pardot.rhombus.cobject;

import java.util.Iterator;
import java.util.List;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/17/13
 */
public class BoundedLazyCQLStatementIterator implements CQLStatementIterator {

	private long limit = 0;
	private long numberRemaining = 0;
	private long size = 0;
	private String CQLTemplate = null;
	private Iterator<Long> shardIdIterator;

	public BoundedLazyCQLStatementIterator(List<Long> shardIds, String CQLTemplate, long limit){
		this.size = (long)shardIds.size();
		this.limit = limit;
		this.numberRemaining = this.limit;
		this.CQLTemplate = CQLTemplate;
		this.shardIdIterator = shardIds.iterator();
	}

	@Override
	public boolean hasNext() {
		return shardIdIterator.hasNext();
	}

	public boolean hasNext(long currentResultCount){
		numberRemaining = limit - currentResultCount;
		if( (this.limit != 0) && (currentResultCount >= this.limit) ){
			return false;
		}
		return this.hasNext();
	}

	@Override
	public String next() {
		return String.format( CQLTemplate, this.shardIdIterator.next().longValue(), numberRemaining);
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