package org.greatfree.framework.streaming.message;

import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.util.IPAddress;

// Created: 03/23/2020, Bing Li
public class SubscribeNotification extends MulticastNotification
{
	private static final long serialVersionUID = -1645857646440309115L;
	
	private String streamKey;
	private String subscriber;
	private IPAddress subscriberIP;

	public SubscribeNotification(String streamKey, String subscriber, IPAddress subscriberIP)
	{
		super(StreamMessageType.SUBSCRIBE_NOTIFICATION);
		this.streamKey = streamKey;
		this.subscriber = subscriber;
		this.subscriberIP = subscriberIP;
	}

	public String getStreamKey()
	{
		return this.streamKey;
	}
	
	public String getSubscriber()
	{
		return this.subscriber;
	}
	
	public IPAddress getSubscriberIP()
	{
		return this.subscriberIP;
	}
}
