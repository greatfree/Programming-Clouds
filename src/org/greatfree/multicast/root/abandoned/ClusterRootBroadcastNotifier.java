package org.greatfree.multicast.root.abandoned;

import java.io.IOException;
import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.reuse.ResourcePool;

/*
 * The class is employed by the CSCluster, the root of a cluster, to send broadcast notifications to each node within the cluster. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
public class ClusterRootBroadcastNotifier<Data extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Data>>
{
	// The TCP client pool that sends notifications. 05/04/2017, Bing Li
	private FreeClientPool clientPool;
	
	// The notification creator. 05/04/2017, Bing Li
	private NotificationCreator creator;
	
	// The pool for the multicastor which broadcasts the notification. 05/04/2017, Bing Li
	private ResourcePool<RootBroadcastNotifierSource<Data, Notification, NotificationCreator>, RootBroadcastNotifier<Data, Notification, NotificationCreator>, RootBroadcastNotifierCreator<Data, Notification, NotificationCreator>, RootBroadcastNotifierDisposer<Data, Notification, NotificationCreator>> notifierPool;

	/*
	 * Initialize the resource pool for the notifier. 05/04/2014, Bing Li
	 */
	public ClusterRootBroadcastNotifier(FreeClientPool clientPool, int multicastorPoolSize, long resourceWaitTime, NotificationCreator creator)
	{
		this.clientPool = clientPool;
		
		this.creator = creator;
		
		this.notifierPool = new ResourcePool<RootBroadcastNotifierSource<Data, Notification, NotificationCreator>, RootBroadcastNotifier<Data, Notification, NotificationCreator>, RootBroadcastNotifierCreator<Data, Notification, NotificationCreator>, RootBroadcastNotifierDisposer<Data, Notification, NotificationCreator>>(multicastorPoolSize, new RootBroadcastNotifierCreator<Data, Notification, NotificationCreator>(), new RootBroadcastNotifierDisposer<Data, Notification, NotificationCreator>(), resourceWaitTime);
	}

	/*
	 * Dispose the resource pool for the notifier. 05/04/2017, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.notifierPool.shutdown();
	}

	/*
	 * Disseminate the notification to all of the distributed nodes within the cluster. 05/04/2017, Bing Li
	 */
	public void notifiy(Data data, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		RootBroadcastNotifier<Data, Notification, NotificationCreator> notifier = this.notifierPool.get(new RootBroadcastNotifierSource<Data, Notification, NotificationCreator>(this.clientPool, rootBranchCount, subBranchCount, this.creator));
		if (notifier != null)
		{
			notifier.disseminate(data);
			this.notifierPool.collect(notifier);
		}
	}
}
