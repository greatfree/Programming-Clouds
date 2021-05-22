package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.WeatherNotification;
import org.greatfree.testing.server.resources.WeatherDB;

/*
 * The thread implements following the pattern of notification queue. It receives a notification that contains the weather information to set the weather instance on the server. 02/11/2016, Bing Li
 */

// Created: 02/10/2016, Bing Li
class SetWeatherThread extends NotificationQueue<WeatherNotification>
{
	/*
	 * Initialize the thread. 02/11/2016, Bing Li
	 */
	public SetWeatherThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * This is the kernel of the notification pattern that sets the weather instance concurrently. 02/11/2016, Bing Li
	 */
	public void run()
	{
		// Declare an instance of WeatherNotification. 02/11/2016, Bing Li
		WeatherNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 02/11/2016, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 02/11/2016, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 02/11/2016, Bing Li
					notification = this.dequeue();
					// Set the value of the weather. 02/11/2016, Bing Li
					WeatherDB.SERVER().setWeather(notification.getWeather());
					// Collect the resource kept by the notification. 02/11/2016, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
