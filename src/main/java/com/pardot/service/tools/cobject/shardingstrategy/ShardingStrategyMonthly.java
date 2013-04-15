package com.pardot.service.tools.cobject.shardingstrategy;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Range;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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

	public ShardingStrategyMonthly(){
	}

	public long getShardKey(long timestamp){
		DateTime d = new DateTime(timestamp, DateTimeZone.UTC);
		long year = (long)d.getYear();
		long month = (long)d.getMonthOfYear();
		return this.offset + ((year - START_YEAR)*12)+month;
	}
}
