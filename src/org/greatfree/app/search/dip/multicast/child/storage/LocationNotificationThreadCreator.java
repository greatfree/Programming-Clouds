package org.greatfree.app.search.dip.multicast.child.storage;

import org.greatfree.app.search.dip.multicast.message.LocationNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 10/08/2018, Bing Li
class LocationNotificationThreadCreator implements NotificationThreadCreatable<LocationNotification, LocationNotificationThread>
{

	@Override
	public LocationNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new LocationNotificationThread(taskSize);
	}

}
