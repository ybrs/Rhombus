package com.pardot.rhombus.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channels;
import java.util.*;
import java.lang.annotation.*;
import java.util.concurrent.*;

import com.github.nedis.codec.CommandArgs;

/**
 * this is controller for redis commands, dispatcher calls this
 */
public class ServerCommands extends Controller {
	

	@RedisCommand(cmd = "PING", returns = "status", authenticate = false)
	public String ping(MessageEvent e, Object[] args) {
		return "PONG";
	}
	
	@RedisCommand(cmd = "SHUTDOWN", returns="OK")
	public void shutdown(MessageEvent e, Object[] args) {
		System.exit(0);
	}
	
}