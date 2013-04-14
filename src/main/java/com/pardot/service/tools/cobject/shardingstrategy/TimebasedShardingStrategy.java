package com.pardot.service.tools.cobject.shardingstrategy;

import com.pardot.service.tools.cobject.CQLStatementIterator;

import java.util.Iterator;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public interface TimebasedShardingStrategy {

	public long getShardKey(UUID uuid);
	public long getShardKey(long timestamp);
	public LazyShardKeyList getShardKeysForRange(long start, long end);
}
