package com.pardot.service.tools.cobject.shardingstrategy;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Range;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/13/13
 */
public class ShardingStrategyMonthly extends TimebasedShardingStrategy {

	public ShardingStrategyMonthly(long start, long end){
		super(start,end);
	}

	public long getShardKey(long timestamp){
		SimpleDateFormat utcYear = new SimpleDateFormat("yyyy");
		SimpleDateFormat utcMonth = new SimpleDateFormat("mm");
		utcYear.setTimeZone(TimeZone.getTimeZone("UTC"));
		utcMonth.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date t = new Date(timestamp*1000);
		long year = Long.parseLong(utcYear.format(t),10);
		long month = Long.parseLong(utcMonth.format(t),10);
		long ret = ((year - START_YEAR)*12)+month;
		return ret;
	}
}
