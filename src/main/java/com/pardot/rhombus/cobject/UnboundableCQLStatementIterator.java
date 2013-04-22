package com.pardot.rhombus.cobject;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import java.util.Iterator;


/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public class UnboundableCQLStatementIterator implements CQLStatementIterator {


	private long limit = 0;
	private long numberRemaining = 0;
	private long size = 0;
	private String CQLTemplate = null;
	private Range<Long> keyRange;
	private Iterator<Long> keyIterator = null;


	public UnboundableCQLStatementIterator(Range<Long> shardKeyList, long limit, CObjectOrdering ordering, String CQLTemplate){
		this.keyRange = shardKeyList;
		ContiguousSet<Long> set = ContiguousSet.create(shardKeyList, DiscreteDomain.longs());
		this.keyIterator = (ordering == CObjectOrdering.ASCENDING) ? set.iterator() : set.descendingIterator();
		this.size = (long)set.size();
		this.limit = limit;
		this.numberRemaining = this.limit;
		this.CQLTemplate = CQLTemplate;
	}

	@Override
	public boolean hasNext() {
		return keyIterator.hasNext();
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
		return String.format( CQLTemplate, keyIterator.next().longValue(), numberRemaining);
	}

	public boolean isBounded(){
		return (keyRange.hasLowerBound() && keyRange.hasUpperBound());
	}

	public long size(){
		return size;
	}

	@Override
	public void remove() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

}
