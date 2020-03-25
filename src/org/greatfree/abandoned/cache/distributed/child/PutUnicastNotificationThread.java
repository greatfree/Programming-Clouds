package org.greatfree.abandoned.cache.distributed.child;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.message.PutNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.cache.distributed.IntegerValue;

/*
 * The thread deals with the notification, PutNotification. Since it needs to process a unicasting notification, it extends the queue of NotificationQueue only, i.e., no any forwarding is required. 07/15/2017, Bing Li
 */

// Created: 07/15/2017, Bing Li
public class PutUnicastNotificationThread<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> extends NotificationQueue<PutNotification<Value>>
{
	// The registry is required. The thread needs to get the map from the registry. 07/15/2017, Bing Li
	private ChildMapRegistry<Key, Value, Factory, DB> registry;
	
	/*
	 * Initialize the thread. 07/15/2017, Bing Li
	 */
	public PutUnicastNotificationThread(int taskSize, ChildMapRegistry<Key, Value, Factory, DB> registry)
	{
		super(taskSize);
		// The registry keeps all of the instances of distributed maps. 07/15/2017, Bing Li
		this.registry = registry;
	}

	/*
	 * The thread to process notifications of PutNotification asynchronously. 07/15/2017, Bing Li
	 */
	@Override
	public void run()
	{
		// The instance of the local distributed map from the registry. 07/22/2017, Bing Li
		DistributedPersistableChildMap<Key, Value, Factory, DB> map;
		// Declare a notification instance of PutNotification. 07/15/2017, Bing Li
		PutNotification<Value> notification;
		// The thread always runs until it is shutdown by the CacheNotificationDispatcher. 07/15/2017, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 07/15/2017, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 07/15/2017, Bing Li
					notification = this.getNotification();
					// Get the instance of distributed map from the registry. 07/15/2017, Bing Li
					map = this.registry.get(notification.getValue().getCacheKey());
					// Put the value into the map. 07/15/2017, Bing Li
					map.put(notification.getValue().getDataKey(), notification.getValue());
					
					// For testing ONLY. 07/20/2017, Bing Li
					// Start testing. 07/20/2017, Bing Li
					IntegerValue v = (IntegerValue)notification.getValue();
					System.out.println("(Key = " + notification.getValue().getDataKey() + ", Value = " + v.getValue() + ") is put into the local distributed map ...");
					// Testing is done. 07/20/2017, Bing Li
					
					// Dispose the notification. 07/15/2017, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 07/15/2017, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
