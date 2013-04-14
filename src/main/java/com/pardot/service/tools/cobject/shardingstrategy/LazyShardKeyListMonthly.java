package com.pardot.service.tools.cobject.shardingstrategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public class LazyShardKeyListMonthly implements LazyShardKeyList {

	private static long START_YEAR = 2000;
	private long start = 0; //number of months at start time
	private long end = 0;   //number of months at end time

	public LazyShardKeyListMonthly(long start, long end){
		this.start = makeShardKey(start);
		this.end = makeShardKey(end);
	}

	public boolean isBounded(){
		return !((start == 0) || (end == 0));
	}

	public long size(){
		//return the number of months (inclusive) between the two dates;
		return end - start;
	}

	public long get(long index){
		return this.start+index;
		//return the shard key of startmonth + index
	}

	protected static long makeShardKey(long timestamp){
		SimpleDateFormat utcYear = new SimpleDateFormat("yyyy");
		SimpleDateFormat utcMonth = new SimpleDateFormat("mm");
		utcYear.setTimeZone(TimeZone.getTimeZone("UTC"));
		utcMonth.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date t = new Date(timestamp*1000);
		long year = Long.parseLong(utcYear.format(t),10);
		long month = Long.parseLong(utcMonth.format(t),10);
		long ret = ((year - START_YEAR)*12)+month;
		return ret;
	};

}
