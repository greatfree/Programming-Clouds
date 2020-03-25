package org.greatfree.message.multicast;

import org.greatfree.util.IPAddress;

// Created: 10/13/2018, Bing Li
public class RPMulticastRequest extends MulticastRequest
{
	private static final long serialVersionUID = -7906827890976356312L;
	
	private IPAddress rpAddress;

	public RPMulticastRequest(int type, IPAddress rpAddress)
	{
		super(type);
		this.rpAddress = rpAddress;
	}
	
	/*
	public RPMulticastRequest(int dataType, IPAddress rpAddress, HashMap<String, IPAddress> childrenServerMap)
	{
		super(dataType, childrenServerMap);
		this.rpAddress = rpAddress;
	}
	*/
	
	public void setAddress(IPAddress address)
	{
		this.rpAddress = address;
	}

	public IPAddress getRPAddress()
	{
		return this.rpAddress;
	}
}
