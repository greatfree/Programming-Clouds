package org.greatfree.framework.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/20/2020, Bing Li
public class SubscribersRequest extends ServerMessage
{
	private static final long serialVersionUID = -6855804504725173334L;
	
	private String publisher;
	private String topic;
	
	private boolean isAll;

	public SubscribersRequest(String publisher, String topic)
	{
		super(StreamMessageType.SUBSCRIBERS_REQUEST);
		this.publisher = publisher;
		this.topic = topic;
		this.isAll = true;
	}

	public SubscribersRequest(String publisher, String topic, boolean isAll)
	{
		super(StreamMessageType.SUBSCRIBERS_REQUEST);
		this.publisher = publisher;
		this.topic = topic;
		this.isAll = isAll;
	}

	public String getPublisher()
	{
		return this.publisher;
	}
	
	public String getTopic()
	{
		return this.topic;
	}
	
	public boolean isAll()
	{
		return this.isAll;
	}
}
