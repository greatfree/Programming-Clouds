package org.greatfree.abandoned.cache.distributed.child;

import java.io.IOException;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.message.UniGetRequest;
import org.greatfree.cache.message.UniGetResponse;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.Tools;

/*
 * The thread receives the unicast request from the root, tries to load the value from the local cache and then replies to the root. 07/22/2017, Bing Li
 */

// Created: 07/22/2017, Bing Li
public class UniGetRequestThread<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> extends NotificationQueue<UniGetRequest<Key>>
{
	// The registry is required. The thread needs to get the map from the registry. 07/15/2017, Bing Li
	private ChildMapRegistry<Key, Value, Factory, DB> registry;

	/*
	 * Initialize the thread. 07/15/2017, Bing Li
	 */
	public UniGetRequestThread(int taskSize, ChildMapRegistry<Key, Value, Factory, DB> registry)
	{
		super(taskSize);
		// The registry keeps all of the instances of distributed maps. 07/15/2017, Bing Li
		this.registry = registry;
	}

	/*
	 * The thread to process unicast request UniGetRequest asynchronously. 07/15/2017, Bing Li
	 */
	@Override
	public void run()
	{
		// The instance of the local distributed map from the registry. 07/22/2017, Bing Li
		DistributedPersistableChildMap<Key, Value, Factory, DB> map;
		// The unicast request. 07/22/2017, Bing Li
		UniGetRequest<Key> request;
		// The unicast response. 07/22/2017, Bing Li
		UniGetResponse<Value> response;
		// The retrieved value. 07/23/2017, Bing Li
		Value v;
		// The thread always runs until it is shutdown by the CacheNotificationDispatcher. 07/15/2017, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 07/15/2017, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the request. 07/15/2017, Bing Li
					request = this.dequeue();
					// Get the instance of distributed map from the registry. 07/15/2017, Bing Li
					map = this.registry.get(request.getRequestedKey().getCacheKey());
					// Retrieve the value from the map according to the key. 07/23/2017, Bing Li
					v = map.get(request.getRequestedKey().getDataKey());
					// Form the response with the retrieved value and other parameters for multicasting request. 07/23/2017, Bing Li
					response = new UniGetResponse<Value>(Tools.generateUniqueKey(), request.getCollaboratorKey(), v);
					// Reply the response to the root. 07/23/2017, Bing Li
					map.respondToRoot(response);
					// Dispose the request. 07/23/2017, Bing Li
					this.disposeMessage(request);
					// Dispose the response. 07/23/2017, Bing Li
					this.dispose(response);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException e)
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
