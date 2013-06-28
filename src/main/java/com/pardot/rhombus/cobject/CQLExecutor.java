package com.pardot.rhombus.cobject;

import com.datastax.driver.core.*;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 6/28/13
 */
public class CQLExecutor {

	private Map<String, PreparedStatement> preparedStatementCache;
	private static Logger logger = LoggerFactory.getLogger(CQLExecutor.class);
	private boolean logCql = false;
	private Session session;

	public CQLExecutor(Session session){
		this.preparedStatementCache = Maps.newConcurrentMap();
		this.session = session;
	}

	public void clearStatementCache(){
		preparedStatementCache.clear();
	}

	public BoundStatement getBoundStatement(Session session, CQLStatement cql){
		PreparedStatement ps = preparedStatementCache.get(cql.getQuery());
		if(ps == null){
			ps = session.prepare(cql.getQuery());
			if(cql.isCacheable()){
				preparedStatementCache.put(cql.getQuery(), ps);
			}
		}

		BoundStatement ret = new BoundStatement(ps);
		ret.bind(cql.getValues());
		return ret;
	}

	public ResultSet executeSync(CQLStatement cql){
		if(logCql) {
			logger.debug("Executing CQL: {}", cql.getQuery());
			//TODO: log values
		}
		if(cql.isPreparable()){
			BoundStatement bs = getBoundStatement(session, cql);
			return session.execute(bs);
		}
		else{
			//just run a normal execute without a prepared statement
			return session.execute(cql.getQuery());
		}
	}

	public ResultSetFuture executeAsync(CQLStatement cql){
		if(logCql) {
			logger.debug("Executing CQL: {}", cql.getQuery());
			//TODO: log values
		}
		if(cql.isPreparable()){
			BoundStatement bs = getBoundStatement(session, cql);
			return session.executeAsync(bs);
		}
		else{
			//just run a normal execute without a prepared statement
			return session.executeAsync(cql.getQuery());
		}
	}
}
