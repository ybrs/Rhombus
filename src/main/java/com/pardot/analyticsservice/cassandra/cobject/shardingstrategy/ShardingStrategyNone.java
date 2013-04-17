package com.pardot.analyticsservice.cassandra.cobject.shardingstrategy;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/15/13
 */
public class ShardingStrategyNone extends TimebasedShardingStrategy {

	public ShardingStrategyNone(){
	}

	public long getShardKey(long timestamp){
		return this.offset + 1;
	}
}