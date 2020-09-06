package org.greatfree.dsf.streaming.message;

import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.util.IPAddress;

// Created: 03/23/2020, Bing Li
public class SubscribeNotification extends MulticastMessage
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
