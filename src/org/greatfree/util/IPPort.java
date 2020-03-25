package org.greatfree.util;

import java.io.Serializable;

// Created: 09/13/2019, Bing Li
public class IPPort implements Serializable
{
	private static final long serialVersionUID = -4407271941074428553L;
	
	private String ip;
	private int port;
	
	public IPPort(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}

	public String getIP()
	{
		return this.ip;
	}
	
	public int getPort()
	{
		return this.port;
	}
}
