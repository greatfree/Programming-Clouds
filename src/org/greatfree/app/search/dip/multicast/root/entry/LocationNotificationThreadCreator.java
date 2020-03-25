package org.greatfree.app.search.dip.multicast.root.entry;

import org.greatfree.app.search.dip.multicast.message.LocationNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 10/07/2018, Bing Li
class LocationNotificationThreadCreator implements NotificationThreadCreatable<LocationNotification, LocationNotificationThread>
{

	@Override
	public LocationNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new LocationNotificationThread(taskSize);
	}

}
