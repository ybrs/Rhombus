package com.pardot.rhombus.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import java.net.InetSocketAddress;

import java.util.concurrent.Executors;
import com.github.nedis.codec.*;
import com.beust.jcommander.JCommander;

import java.lang.Class;
import java.util.*;

public class Server {

	private final String host;
	private int port;
	private DefaultChannelGroup channelGroup;
	private ServerChannelFactory serverFactory;
	private CommandDispatcher dispatcher;

	private ChannelGroup channels;
	private Timer timer;

	private long cnt = 0;

	private ServerBootstrap bootstrap;

	private ServerCommandLineArguments jct;
	private ChannelPipelineFactory pipelineFactory;

	// public List<RedisListener> listeners;

	public Server(ServerCommandLineArguments jct) {
		this.host = jct.host;
		this.port = jct.port;
		this.jct = jct;
		System.out.println("init");
	}

	public void prepare() {
		System.out.println("prepare");
		channels = new DefaultChannelGroup();
		timer = new HashedWheelTimer();
		this.serverFactory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		this.channelGroup = new DefaultChannelGroup(this + "-channelGroup");

		/*
		 * We build up the dispatcher now ! Wish java had mixins
		 */
		List<Class<?>> klasses = new ArrayList<Class<?>>();
		klasses.add(ZStoreCommands.class);
		klasses.add(ServerCommands.class);
		dispatcher = new CommandDispatcher(klasses);

		final Map<String, Controller> subscriptions = new ConcurrentHashMap<String, Controller>();

		pipelineFactory = new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {

				cnt = cnt + 1;

				if (cnt > Long.MAX_VALUE - 1) {
					cnt = 0;
				}

				System.out.println("client connecting");
				
				System.out.println("client connected");
				
				ServerHandler handler = new ServerHandler(channelGroup);
				handler.setClient();
				handler.setDispatcher(dispatcher);

				ChannelPipeline pipeline = Channels.pipeline();
				// pipeline.addLast("encoder", Encoder.getInstance());
				// pipeline.addLast("encoder", Command);
				pipeline.addLast("decoder", new RedisDecoder());
				pipeline.addLast("handler", handler);
				return pipeline;
			}
		};

	}

	public void start() {
		ServerBootstrap bootstrap = new ServerBootstrap(this.serverFactory);
		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setPipelineFactory(pipelineFactory);

		Channel channel = bootstrap.bind(new InetSocketAddress(this.host,
				this.port));
		// if (!channel.isBound()) {
		// this.stop();
		// }

		this.channelGroup.add(channel);

	}

	public void stop() {
		if (this.channelGroup != null) {
			this.channelGroup.close();
		}
		if (this.serverFactory != null) {
			this.serverFactory.releaseExternalResources();
		}
	}

	public static void main(String[] args) {

		ServerCommandLineArguments jct_ = new ServerCommandLineArguments();
		new JCommander(jct_, args);

		System.out.println("listening on " + jct_.host + ":" + jct_.port + "");

		final Server server = new Server(jct_);

		server.prepare();

		final long timeToWait = 1000;

		while (true) {
			try {
				server.start();
				break;
			} catch (Exception e) {
				server.port = server.port + 1;
				try {
					Thread.sleep(timeToWait);
				} catch (InterruptedException i1) {
					// pass
				}
			}
		}

		System.out.println("Mergen Server listening for commands on "
				+ server.port);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.stop();
			}
		});
	}
}
