package org.greatfree.testing.stress.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 11/07/2021, Bing Li
public class StressNotification extends ClusterNotification
{
	private static final long serialVersionUID = 4829149551450267931L;
	
	private long index;
	private String message;

	public StressNotification(long index, String message)
	{
		super(MulticastMessageType.UNICAST_NOTIFICATION, StressAppID.STRESS_NOTIFICATION);
		this.index = index;
		this.message = message;
	}
	
	public long getIndex()
	{
		return this.index;
	}

	public String getMessage()
	{
		return this.message;
	}
	
	public String toString()
	{
		return "Notification-" + this.index + " : " + this.message;
	}
}
