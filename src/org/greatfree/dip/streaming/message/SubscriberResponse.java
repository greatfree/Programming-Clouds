package org.greatfree.dip.streaming.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

// Created: 03/23/2020, Bing Li
public class SubscriberResponse extends ServerMessage
{
	private static final long serialVersionUID = 8072793902683316776L;
	
	private IPAddress ip;

	public SubscriberResponse(IPAddress ip)
	{
		super(StreamMessageType.SUBSCRIBER_RESPONSE);
		this.ip = ip;
	}

	public IPAddress getIP()
	{
		return this.ip;
	}
}
