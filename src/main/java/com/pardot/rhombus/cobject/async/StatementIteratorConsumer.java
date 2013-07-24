package com.pardot.rhombus.cobject.async;

import com.datastax.driver.core.*;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.pardot.rhombus.cobject.BoundedCQLStatementIterator;
import com.pardot.rhombus.cobject.CQLExecutor;
import com.pardot.rhombus.cobject.CQLStatement;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
	private List<StatementConsumer> consumers;

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
		consumers = Lists.newArrayList();
		while(statementIterator.hasNext()) {
			StatementConsumer consumer = new StatementConsumer(statementIterator.next(), cqlExecutor, statementTimeout);
			consumers.add(consumer);
		}
	}

	public void join() {
		for(StatementConsumer consumer : consumers) {
			consumer.join();
		}
	}
}
