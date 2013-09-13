package com.pardot.rhombus.server;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channels;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.math.BigInteger;

import java.security.SecureRandom;
import java.util.*;

import com.github.nedis.codec.*;


public class ServerHandler extends SimpleChannelUpstreamHandler {

    private final ChannelGroup channelGroup;    
    private CommandDispatcher dispatcher;
    private Controller controller;
    public Base base;
	private Map<String, Controller> pubsublist;

    public ServerHandler(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;        
    }

    public void setClient(){
        this.base = new Base();
    }

    public void setDispatcher(CommandDispatcher dispatcher){
        this.dispatcher = dispatcher; 
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	System.out.println("disconnected - " + this.base.getClientName());
    	this.base.clientDisconnected();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.channelGroup.add(e.getChannel());
    }

    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof MultiBulkReply){
            Object[] args = ((MultiBulkReply) e.getMessage()).get();
            int size = ((MultiBulkReply) e.getMessage()).getSize();

            // which controller we need
            String cmd = new String((byte[])args[0]);
            Class klass = dispatcher.getClassForCommand(cmd.toUpperCase());

            if (klass==null){
                ServerReply sr = new ServerReply();
                e.getChannel().write(sr.replyError("method not implemented"));
                
                for (int i = 0; i < args.length; i++) {
					String mcmd = new String((byte[])args[i]);
				}
                return;
            }

            Constructor co = klass.getConstructor();
            controller = (Controller)co.newInstance();
            controller.base = base;
            controller.context = ctx;

            if (size > 0){
                dispatcher.dispatch(controller, e, args);
            } else {
                // TODO: ??? 
                ServerReply sr = new ServerReply();
                e.getChannel().write(sr.replyOK());
            }
            // e.getChannel().write(replyOK());
        } else {
            ServerReply sr = new ServerReply();
            e.getChannel().write(sr.replyOK());
        }
    }

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		System.out.println("Disconnected >>> " + this.base.getClientName());
	}

	
}

