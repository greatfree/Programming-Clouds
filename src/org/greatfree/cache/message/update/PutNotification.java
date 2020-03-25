package org.greatfree.cache.message.update;

import java.util.HashMap;

import org.greatfree.abandoned.cache.distributed.CacheValue;
import org.greatfree.cache.message.CacheMessageType;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

// Created: 07/16/2017, Bing Li
public class PutNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = 4645159484625099587L;
	
	private CacheValue value;

//	public PutNotification(String key, CacheValue value)
	public PutNotification(CacheValue value)
	{
		super(CacheMessageType.PUT_NOTIFICATION);
		this.value = value;
	}

//	public PutNotification(String key, HashMap<String, IPAddress> childrenServers, CacheValue value)
	public PutNotification(HashMap<String, IPAddress> childrenServers, CacheValue value)
	{
		super(CacheMessageType.PUT_NOTIFICATION, childrenServers);
		this.value = value;
	}
	
	public CacheValue getValue()
	{
		return this.value;
	}
}
