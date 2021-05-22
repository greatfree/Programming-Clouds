package org.greatfree.app.search.dip.multicast.child.storage;

import org.greatfree.app.search.dip.multicast.message.LocationNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 10/08/2018, Bing Li
class LocationNotificationThreadCreator implements NotificationQueueCreator<LocationNotification, LocationNotificationThread>
{

	@Override
	public LocationNotificationThread createInstance(int taskSize)
	{
		return new LocationNotificationThread(taskSize);
	}

}
