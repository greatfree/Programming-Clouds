package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.WeatherNotification;

/*
 * The class creates an instance of the thread, SetWeatherThread. It is used by the NotificationDispatcher to manage the thread count and relevant resources. 02/15/2016, Bing Li
 */

// Created: 02/15/2016, Bing Li
class SetWeatherThreadCreator implements NotificationQueueCreator<WeatherNotification, SetWeatherThread>
{
	@Override
	public SetWeatherThread createInstance(int taskSize)
	{
		return new SetWeatherThread(taskSize);
	}
}
