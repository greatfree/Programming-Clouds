package org.greatfree.dsf.streaming.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 03/20/2020, Bing Li
public class SubscribersResponse extends ServerMessage
{
	private static final long serialVersionUID = 8181469985482243424L;
	
	private List<String> subscribers;
	private String selectedSubscriber;

	public SubscribersResponse(List<String> subscribers)
	{
		super(StreamMessageType.SUBSCRIBERS_RESPONSE);
		this.subscribers = subscribers;
	}

	public SubscribersResponse(String subscriber)
	{
		super(StreamMessageType.SUBSCRIBERS_RESPONSE);
		this.selectedSubscriber = subscriber;
	}

	public List<String> getSubscribers()
	{
		return this.subscribers;
	}
	
	public String getSelectedSubscriber()
	{
		return this.selectedSubscriber;
	}
}
