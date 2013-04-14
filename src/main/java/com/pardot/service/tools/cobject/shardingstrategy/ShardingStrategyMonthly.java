package com.pardot.service.tools.cobject.shardingstrategy;

import com.datastax.driver.core.utils.UUIDs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public class ShardingStrategyMonthly implements TimebasedShardingStrategy {

	public long getShardKey(UUID uuid){
		return getShardKey(UUIDs.unixTimestamp(uuid));
	}

	public long getShardKey(long timestamp){
		return LazyShardKeyListMonthly.makeShardKey(timestamp);
	}

	public LazyShardKeyList getShardKeysForRange(long start, long end){
		return new LazyShardKeyListMonthly(start,end);
	};
}
