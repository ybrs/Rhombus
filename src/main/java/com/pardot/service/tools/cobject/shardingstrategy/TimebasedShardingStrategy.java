package com.pardot.service.tools.cobject.shardingstrategy;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Range;
import com.pardot.service.tools.cobject.CQLStatementIterator;

import java.util.Iterator;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public abstract class TimebasedShardingStrategy {


	public static long START_YEAR = 2000;
	private long start = 0; //number of months at start time
	private long end = 0;   //number of months at end time
	protected long offset = 0;

	public TimebasedShardingStrategy(long start, long end, long offset){
		this.start = getShardKey(start);
		this.end = getShardKey(end);
		this.offset = offset;
	}

	public long getShardKey(UUID uuid){
		return getShardKey(UUIDs.unixTimestamp(uuid));
	}

	public abstract long getShardKey(long timestamp);

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
