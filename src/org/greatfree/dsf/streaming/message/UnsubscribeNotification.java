package org.greatfree.dsf.streaming.message;

import org.greatfree.message.multicast.MulticastNotification;

// Created: 03/23/2020, Bing Li
public class UnsubscribeNotification extends MulticastNotification
{
	private static final long serialVersionUID = 8418225857650621992L;

	private String streamKey;
	private String subscriber;

	public UnsubscribeNotification(String streamKey, String subscriber)
	{
		super(StreamMessageType.UNSUBSCRIBE_NOTIFICATION);
		this.streamKey = streamKey;
		this.subscriber = subscriber;
	}

	public String getStreamKey()
	{
		return this.streamKey;
	}
	
	public String getSubscriber()
	{
		return this.subscriber;
	}
}
