package org.greatfree.dsf.streaming.unicast.child;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

// Created: 03/22/2020, Bing Li
public class StreamRegistry
{
//	private Map<String, List<String>> subscribers;
	private Map<String, Map<String, IPAddress>> ips;
	
	private StreamRegistry()
	{
//		this.subscribers = new ConcurrentHashMap<String, List<String>>();
		this.ips = new ConcurrentHashMap<String, Map<String, IPAddress>>();
	}
	
	private static StreamRegistry instance = new StreamRegistry();
	
	public static StreamRegistry UNICAST()
	{
		if (instance == null)
		{
			instance = new StreamRegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
//		this.subscribers.clear();
		this.ips.clear();
	}

	public void addSubscriber(String streamKey, String subscriber, IPAddress ip)
	{
		/*
		if (!this.subscribers.containsKey(streamKey))
		{
			this.subscribers.put(streamKey, new ArrayList<String>());
		}
		this.subscribers.get(streamKey).add(subscriber);
		*/
		if (!this.ips.containsKey(streamKey))
		{
			this.ips.put(streamKey, new HashMap<String, IPAddress>());
		}
//		this.ips.get(streamKey).put(ip.getIPKey(), ip);
		this.ips.get(streamKey).put(subscriber, ip);
	}

	public void removeSubscriber(String streamKey, String subscriber)
	{
		/*
		if (this.subscribers.containsKey(streamKey))
		{
			this.subscribers.get(streamKey).remove(subscriber);
		}
		*/
		if (this.ips.containsKey(streamKey))
		{
			this.ips.get(streamKey).remove(subscriber);
		}
	}

	/*
	public List<String> getSubscribers(String streamKey)
	{
		if (this.subscribers.containsKey(streamKey))
		{
			return this.subscribers.get(streamKey);
		}
		return StreamConfig.NO_SUBSCRIBERS;
	}
	*/
	
	public Map<String, IPAddress> getSubscriberIPs(String streamKey)
	{
		if (this.ips.containsKey(streamKey))
		{
			return this.ips.get(streamKey);
		}
		return UtilConfig.NO_IPS;
	}
}
