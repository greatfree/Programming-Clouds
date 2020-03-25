package org.greatfree.dip.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/18/2020, Bing Li
public class UnsubscribeStreamNotification extends ServerMessage
{
	private static final long serialVersionUID = -3291245932560774124L;
	
	private String subscriber;
	private String publisher;
	private String topic;

	public UnsubscribeStreamNotification(String subscriber, String publisher, String topic)
	{
		super(StreamMessageType.UNSUBSCRIBE_STREAM_NOTIFICATION);
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
