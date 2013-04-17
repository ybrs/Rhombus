package com.pardot.analyticsservice.cassandra.cobject.shardingstrategy;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/15/13
 */
public class ShardStrategyException extends Exception {
	public ShardStrategyException(String message){
		super(message);
	}
}
