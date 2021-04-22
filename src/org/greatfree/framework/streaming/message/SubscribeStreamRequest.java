package org.greatfree.framework.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/18/2020, Bing Li
public class SubscribeStreamRequest extends ServerMessage
{
	private static final long serialVersionUID = -9126179027665730915L;
	
	private String subscriber;
	private String publisher;
	private String topic;

	public SubscribeStreamRequest(String subscriber, String publisher, String topic)
	{
		super(StreamMessageType.SUBSCRIBE_STREAM_REQUEST);
		this.subscriber = subscriber;
		this.publisher = publisher;
		this.topic = topic;
	}

	public String getSubscriber()
	{
		return this.subscriber;
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
