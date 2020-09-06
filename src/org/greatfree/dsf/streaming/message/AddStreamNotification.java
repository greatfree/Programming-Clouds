package org.greatfree.dsf.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/18/2020, Bing Li
public class AddStreamNotification extends ServerMessage
{
	private static final long serialVersionUID = 6336592801869129296L;
	
	private String publisher;
	private String topic;

	public AddStreamNotification(String publisher, String topic)
	{
		super(StreamMessageType.ADD_STREAM_NOTIFICATION);
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
