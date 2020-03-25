package org.greatfree.app.search.dip.multicast.message;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 10/07/2018, Bing Li
public class LocationNotification extends MulticastMessage
{
	private static final long serialVersionUID = 3156013322130770872L;
	
	private String userKey;
	private boolean isInternational;

	public LocationNotification(String userKey, boolean isInternational)
	{
		super(SearchMessageType.LOCATION_NOTIFICATION);
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
