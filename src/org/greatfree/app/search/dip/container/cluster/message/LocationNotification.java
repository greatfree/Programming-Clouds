package org.greatfree.app.search.dip.container.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/14/2019, Bing Li
public class LocationNotification extends Notification
{
	private static final long serialVersionUID = 4742089141279809955L;

	private String userKey;
	private boolean isInternational;

	public LocationNotification(String userKey, boolean isInternational)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, SearchApplicationID.LOCATION_NOTIFICATION);
		this.userKey = userKey;
		this.isInternational = isInternational;
	}
	
	public String getUserKey()
	{
		return this.userKey;
	}

	public boolean isInternational()
	{
		return this.isInternational;
	}
}
