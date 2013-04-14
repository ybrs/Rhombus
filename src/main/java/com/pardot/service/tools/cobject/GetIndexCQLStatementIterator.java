package com.pardot.service.tools.cobject;

import com.pardot.service.tools.cobject.shardingstrategy.LazyShardKeyList;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public class GetIndexCQLStatementIterator implements CQLStatementIterator {


	private long count = 0;
	private long limit = 0;
	private String CQLTemplate = null;

	private LazyShardKeyList shardKeyList = null;


	//TODO: figure out what to do with ASC ordering
	public GetIndexCQLStatementIterator(LazyShardKeyList shardKeyList, long limit, String CQLTemplate ){
		this.shardKeyList = shardKeyList;
		this.limit = limit;
		this.CQLTemplate = CQLTemplate;
	}

	@Override
	public boolean hasNext() {
		if(!shardKeyList.isBounded()){
			return true;
		}
		return (count < shardKeyList.size());
	}

	public boolean hasNext(long currentResultCount){
		if( (this.limit != 0) && (currentResultCount >= this.limit) ){
			return false;
		}
		return this.hasNext();
	}

	@Override
	public String next() {
		String ret = String.format( this.CQLTemplate, shardKeyList.get(count) );
		count++;
		return ret;
	}

	public boolean isBounded(){
		return shardKeyList.isBounded();
	}

	public long size(){
		return shardKeyList.size();
	}

	@Override
	public void remove() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

}
