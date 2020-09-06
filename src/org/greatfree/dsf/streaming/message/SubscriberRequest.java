package org.greatfree.dsf.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/23/2020, Bing Li
public class SubscriberRequest extends ServerMessage
{
	private static final long serialVersionUID = -5094573996908876443L;

	private String publisher;
	private String topic;

	public SubscriberRequest(String publisher, String topic)
	{
		super(StreamMessageType.SUBSCRIBER_REQUEST);
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
}
