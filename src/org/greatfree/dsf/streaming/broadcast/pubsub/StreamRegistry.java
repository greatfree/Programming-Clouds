package org.greatfree.dsf.streaming.broadcast.pubsub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.dsf.streaming.RegisteredStream;
import org.greatfree.dsf.streaming.Stream;
import org.greatfree.dsf.streaming.StreamConfig;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;

// Created: 03/18/2020, Bing Li
public class StreamRegistry
{
	private Map<String, Stream> streams;
	private Map<String, IPAddress> ips;

	private StreamRegistry()
	{
		this.streams = new ConcurrentHashMap<String, Stream>();
		this.ips = new ConcurrentHashMap<String, IPAddress>();
	}
	
	private static StreamRegistry instance = new StreamRegistry();
	
	public static StreamRegistry PUBSUB()
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
		this.streams.clear();
		this.streams = null;
	}
	
	public List<RegisteredStream> getAllStreams()
	{
		List<RegisteredStream> streams = new ArrayList<RegisteredStream>();
		for (Stream entry : this.streams.values())
		{
			streams.add(entry.getStream());
		}
		return streams;
	}
	
	public void addStream(String publisher, String topic)
	{
		Stream s = new Stream(publisher, topic);
		this.streams.put(s.getKey(), s);
	}
	
	public void removeStream(String publisher, String topic)
	{
		this.streams.remove(Stream.generateKey(publisher, topic));
	}
	
	public boolean subscribe(String publisher, String topic, String subscriber)
	{
		System.out.println("StreamRegistry-subscribe(): subscriber = " + subscriber);
		String streamKey = Stream.generateKey(publisher, topic);
		if (this.streams.containsKey(streamKey))
		{
			this.streams.get(streamKey).addSubscriber(subscriber);
			return true;
		}
		return false;
	}
	
	public void unsubscribe(String publisher, String topic, String subscriber)
	{
		String streamKey = Stream.generateKey(publisher, topic);
		if (this.streams.containsKey(streamKey))
		{
			this.streams.get(streamKey).removeSubscriber(subscriber);
		}
	}
	
	public List<String> getSubscribers(String publisher, String topic)
	{
		String streamKey = Stream.generateKey(publisher, topic);
		if (this.streams.containsKey(streamKey))
		{
			if (this.streams.get(streamKey).getSubscribers().size() > 0)
			{
				return this.streams.get(streamKey).getSubscribers();
			}
		}
		return StreamConfig.NO_SUBSCRIBERS;
	}
	
	public String getSubscriber(String publisher, String topic)
	{
		String streamKey = Stream.generateKey(publisher, topic);
		if (this.streams.containsKey(streamKey))
		{
			if (this.streams.get(streamKey).getSubscribers().size() > 0)
			{
				return Rand.getRandomListElement(this.streams.get(streamKey).getSubscribers());
			}
		}
		return StreamConfig.NO_SUBSCRIBER;
	}
	
	public void addAddress(String subscriber, IPAddress ip)
	{
		this.ips.put(subscriber, ip);
	}
	
	public IPAddress getAddress(String subscriber)
	{
		return this.ips.get(subscriber);
	}
}
