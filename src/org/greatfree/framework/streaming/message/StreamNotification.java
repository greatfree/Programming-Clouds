package org.greatfree.framework.streaming.message;

import org.greatfree.framework.streaming.StreamData;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 03/18/2020, Bing Li
public class StreamNotification extends MulticastNotification
{
	private static final long serialVersionUID = 7264578299029579295L;
	
	private StreamData data;
	
	private String childKey;

	public StreamNotification(StreamData data)
	{
		super(StreamMessageType.STREAM_NOTIFICATION);
		this.data = data;
	}

	public StreamData getData()
	{
		return this.data;
	}
	
	public String getChildKey()
	{
		return this.childKey;
	}
	
	public void setChildKey(String ck)
	{
		this.childKey = ck;
	}
}
