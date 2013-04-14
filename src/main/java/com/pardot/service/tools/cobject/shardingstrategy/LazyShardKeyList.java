package com.pardot.service.tools.cobject.shardingstrategy;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public interface LazyShardKeyList {
	public boolean isBounded();
	public long size();
	public long get(long index);
}
