package org.greatfree.dsf.streaming;

import java.io.Serializable;

import org.greatfree.util.UtilConfig;

// Created: 03/18/2020, Bing Li
public class StreamData implements Serializable
{
	private static final long serialVersionUID = -5103913675568760113L;
	
	private String publisher;
	private String topic;
	private String content;
	
	public StreamData(String publisher, String topic, String content)
	{
		this.publisher = publisher;
		this.topic = topic;
		this.content = content;
	}

	public String getPublisher()
	{
		return this.publisher;
	}
	
	public String getTopic()
	{
		return this.topic;
	}
	
	public String getContent()
	{
		return this.content;
	}
	
	public String toString()
	{
		return this.publisher + UtilConfig.COMMA + this.topic + UtilConfig.COMMA + this.content;
	}
}
