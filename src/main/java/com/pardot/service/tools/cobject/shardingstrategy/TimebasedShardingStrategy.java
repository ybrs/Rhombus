package com.pardot.service.tools.cobject.shardingstrategy;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Range;

import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ShardingStrategyMonthly.class, name = "ShardingStrategyMonthly"),
		@JsonSubTypes.Type(value = ShardingStrategyNone.class, name = "ShardingStrategyNone")
})
public abstract class TimebasedShardingStrategy {


	public static long START_YEAR = 2000;

	protected long offset = 0;

	public TimebasedShardingStrategy(){
	}

	public TimebasedShardingStrategy(long offset){
		this.offset = offset;
	}

	public long getShardKey(UUID uuid){
		return getShardKey(UUIDs.unixTimestamp(uuid));
	}

	public abstract long getShardKey(long timestamp);

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public Range<Long> getShardKeyRange(long timestampStart, long timestampEnd, boolean inclusive) throws ShardStrategyException {
		long start = getShardKey(timestampStart);
		long end = getShardKey(timestampEnd);

		if(start == 0){
			//unbounded start
			if(end == 0){
				//unbounded start and unbounded end
				//THIS IS NOT ALLOWED. Throw an exception here
				throw new ShardStrategyException("Time range must have either an upper or lower bound");
			}
			else{
				//unbounded start and bounded end
				return inclusive ? Range.atMost(end) : Range.lessThan(end);
			}
		}
		else{
			//bounded start
			if(end == 0){
				//bounded start and unbounded end
				return inclusive ? Range.atLeast(start) : Range.greaterThan(start);
			}
			else{
				//bounded start and bounded end
				return inclusive ? Range.closed(start,end) : Range.open(start, end);
			}
		}
	}
}
