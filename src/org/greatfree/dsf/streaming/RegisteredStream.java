package org.greatfree.dsf.streaming;

import java.io.Serializable;

import org.greatfree.util.UtilConfig;

// Created: 03/20/2020, Bing Li
public class RegisteredStream implements Serializable
{
	private static final long serialVersionUID = -5796551791682325882L;
	
	private String publisher;
	private String topic;
	
	public RegisteredStream(String publisher, String topic)
	{
		this.publisher = publisher;
		this.topic = topic;
	}
	
	public String getPublisher()
	{
		return this.publisher;
	}
	
	public String getTopic()
	{
		return this.topic;
	}
	
	public String toString()
	{
		return this.publisher + UtilConfig.COLON + this.topic;
	}

}
