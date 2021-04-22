package org.greatfree.framework.streaming;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greatfree.util.Tools;

// Created: 03/18/2020, Bing Li
public class Stream
{
	private String key;
	private String publisher;
	private String topic;
	private List<String> subscribers;
	
	public Stream(String publisher, String topic)
	{
		this.key = Stream.generateKey(publisher, topic);
		this.publisher = publisher;
		this.topic = topic;
		this.subscribers = new CopyOnWriteArrayList<String>();
	}

	public String getKey()
	{
		return this.key;
	}
	
	public String getPublisher()
	{
		return this.publisher;
	}
	
	public String getTopic()
	{
		return this.topic;
	}
	
	public void addSubscriber(String subscriber)
	{
//		this.subscribers.add(Stream.generateKey(subscriber));
		this.subscribers.add(subscriber);
	}
	
	public void removeSubscriber(String subscriber)
	{
//		this.subscribers.remove(Stream.generateKey(subscriber));
		this.subscribers.remove(subscriber);
	}
	
	public List<String> getSubscribers()
	{
		return this.subscribers;
	}
	
	public static String generateKey(String publisher, String topic)
	{
		return Tools.getHash(publisher + topic);
	}

	/*
	public static String generateKey(String subscriber)
	{
		return Tools.getHash(subscriber);
	}
	*/
	
	public RegisteredStream getStream()
	{
		return new RegisteredStream(this.publisher, this.topic);
	}
}
