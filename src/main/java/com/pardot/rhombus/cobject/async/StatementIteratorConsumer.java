package com.pardot.rhombus.cobject.async;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.pardot.rhombus.cobject.BoundedCQLStatementIterator;
import com.pardot.rhombus.cobject.CQLExecutor;
import com.pardot.rhombus.cobject.CQLStatement;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static Logger logger = LoggerFactory.getLogger(StatementIteratorConsumer.class);
	private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	public final Timer latencies = Metrics.newTimer(StatementIteratorConsumer.class, "latencies", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

	private final BoundedCQLStatementIterator statementIterator;
	private CQLExecutor cqlExecutor;
	private final CountDownLatch shutdownLatch;
	private final long statementTimeout;


	public StatementIteratorConsumer(BoundedCQLStatementIterator statementIterator, CQLExecutor cqlExecutor, long statementTimeout) {
		this.statementIterator = statementIterator;
		this.cqlExecutor = cqlExecutor;
		this.statementTimeout = statementTimeout;
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
		logger.debug("awaitUninterruptibly with timeout {}ms", statementTimeout);
		awaitUninterruptibly(shutdownLatch, statementTimeout, TimeUnit.MILLISECONDS);
	}

	protected void handle(CQLStatement statement) {
		final TimerContext timerContext = latencies.time();
		ResultSetFuture future = this.cqlExecutor.executeAsync(statement);
		Futures.addCallback(future, new FutureCallback<ResultSet>() {
			@Override
			public void onSuccess(final ResultSet result) {
				shutdownLatch.countDown();
			}
			@Override
			public void onFailure(final Throwable t) {
				//TODO Stop processing and return error
				logger.error("Error during async request: {}", t);
				shutdownLatch.countDown();
			}
		}, executorService);
	}
}
