package com.pardot.rhombus.server;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

class Base  {
	/**
	 * we init this once for every connection, so you can use it like a session,
	 * 
	 */
	

	public Base() {
	}

	/*
	 * get internal name for client
	 */
	public String getClientName() {
		String localhostname;
		try {
			localhostname = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			localhostname = "unknown";
		}

		String n = ManagementFactory.getRuntimeMXBean().getName();

		String clientname = localhostname + "-" + n ;
		return clientname;
	}
	
	public void clientDisconnected() {
		System.out.println(">>>> >>>> Client Disconnected");
	}

}
