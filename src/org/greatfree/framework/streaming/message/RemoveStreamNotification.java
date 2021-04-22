package org.greatfree.framework.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/18/2020, Bing Li
public class RemoveStreamNotification extends ServerMessage
{
	private static final long serialVersionUID = 1531496725322537735L;
	
	private String publisher;
	private String topic;

	public RemoveStreamNotification(String publisher, String topic)
	{
		super(StreamMessageType.REMOVE_STREAM_NOTIFICATION);
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
