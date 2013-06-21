package com.pardot.rhombus.cobject.async;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.pardot.rhombus.cobject.BoundedCQLStatementIterator;
import com.pardot.rhombus.cobject.CQLStatement;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 6/21/13
 */
public class StatementIteratorConsumer {

	private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	public final Timer latencies = Metrics.newTimer(StatementIteratorConsumer.class, "latencies", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

	private final Session session;
	private final BoundedCQLStatementIterator statementIterator;
	private final Map<String,BoundStatement> boundStatementCache;
	private final CountDownLatch shutdownLatch;


	public StatementIteratorConsumer(Session session, BoundedCQLStatementIterator statementIterator, Map<String,BoundStatement> boundStatementCache) {
		this.session = session;
		this.statementIterator = statementIterator;
		this.boundStatementCache = boundStatementCache;
		shutdownLatch = new CountDownLatch((new Long(statementIterator.size())).intValue());
	}

	public void start() {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				request();
			}
		});
	}

	private void request() {
		while(statementIterator.hasNext()) {
			handle(statementIterator.next());
		}
	}

	public void join() {
		//TODO add timeout
		awaitUninterruptibly(shutdownLatch);
	}

	protected void handle(CQLStatement statement) {
		final TimerContext timerContext = latencies.time();
		ResultSetFuture future;
		if(statement.isPreparable()){
			//Do prepared statement
			BoundStatement bs = boundStatementCache.get(statement.getQuery());
			if(bs == null){
				PreparedStatement preparedStatement = session.prepare(statement.getQuery());
				bs = new BoundStatement(preparedStatement);
				if(statement.isCacheable()){
					boundStatementCache.put(statement.getQuery(),bs);
				}
			}

			future = session.executeAsync(bs.bind(statement.getValues()));
		} else {
			//just run a normal execute without a prepared statement
			future = session.executeAsync(statement.getQuery());
		}
		Futures.addCallback(future, new FutureCallback<ResultSet>() {
			@Override
			public void onSuccess(final ResultSet result) {
				shutdownLatch.countDown();
			}
			@Override
			public void onFailure(final Throwable t) {
				//TODO Stop processing and return error
				System.err.println("Error during request: " + t);
				shutdownLatch.countDown();
			}
		}, executorService);
	}
}
