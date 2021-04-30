package edu.chainnet.sc.message;

import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;
import org.greatfree.util.UniqueKey;
import org.greatfree.util.UtilConfig;

/*
 * DSNode represents the term, the "distributed system nodes". The node is a regular one for distributed systems. 10/18/2020, Bing Li
 */

// Created: 10/18/2020, Bing Li
public class DSNode extends UniqueKey
{
	private static final long serialVersionUID = 8003361985106056340L;
	
	private String name;
	private IPAddress ip;
	private String serviceName;
	private String description;

	public DSNode(String name, IPAddress ip, String serviceName, String description)
	{
		super(DSNode.getKey(name));
		this.name = name;
		this.ip = ip;
		this.serviceName = serviceName;
		this.description = description;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public IPAddress getIP()
	{
		return this.ip;
	}
	
	public String getServiceName()
	{
		return this.serviceName;
	}
	
	public String getDescription()
	{
		return this.description;
	}

	public static String getKey(String name)
	{
		return Tools.getHash(name);
	}
	
	public String toString()
	{
		return this.name + UtilConfig.COLON + this.ip + UtilConfig.COLON + this.serviceName + UtilConfig.COLON + this.description;
	}
}
