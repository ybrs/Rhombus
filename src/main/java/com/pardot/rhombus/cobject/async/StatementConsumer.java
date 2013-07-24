package com.pardot.rhombus.cobject.async;

import com.datastax.driver.core.Host;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.pardot.rhombus.cobject.BoundedCQLStatementIterator;
import com.pardot.rhombus.cobject.CQLExecutor;
import com.pardot.rhombus.cobject.CQLStatement;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class StatementConsumer {

	private static Logger logger = LoggerFactory.getLogger(StatementConsumer.class);
	private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final CQLStatement statement;
	private CQLExecutor cqlExecutor;
	private final CountDownLatch shutdownLatch;
	private final long statementTimeout;

	public StatementConsumer(CQLStatement statement, CQLExecutor cqlExecutor, long statementTimeout) {
		this.statement = statement;
		this.cqlExecutor = cqlExecutor;
		this.statementTimeout = statementTimeout;
		shutdownLatch = new CountDownLatch(1);
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
		handle(statement);
	}

	public void join() {
		logger.debug("awaitUninterruptibly with timeout {}ms", statementTimeout);
		awaitUninterruptibly(shutdownLatch, statementTimeout, TimeUnit.MILLISECONDS);
	}

	protected void handle(CQLStatement statement) {
		ResultSetFuture future = this.cqlExecutor.executeAsync(statement);
		Futures.addCallback(future, new FutureCallback<ResultSet>() {
			@Override
			public void onSuccess(final ResultSet result) {
				Host queriedHost = result.getExecutionInfo().getQueriedHost();
				logger.debug("queried host: {} in datacenter {}", queriedHost, queriedHost.getDatacenter());
				Metrics.defaultRegistry().newMeter(StatementConsumer.class, "queriedhost." + queriedHost.getDatacenter(), queriedHost.getDatacenter(), TimeUnit.SECONDS).mark();
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
