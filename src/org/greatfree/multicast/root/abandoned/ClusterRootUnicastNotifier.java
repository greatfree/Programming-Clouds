package org.greatfree.multicast.root.abandoned;

import java.io.IOException;
import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.reuse.ResourcePool;

/*
 * The class is employed by the CSCluster, the root of a cluster, to send unicast notifications to one particular node within the cluster. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
public class ClusterRootUnicastNotifier<Data extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Data>>
{
	// The TCP client pool that sends notifications. 05/04/2017, Bing Li
	private FreeClientPool clientPool;
	
	// The notification creator. 05/04/2017, Bing Li
	private NotificationCreator creator;

	// The pool for the multicastor which broadcasts the notification. 05/04/2017, Bing Li
	private ResourcePool<RootUnicastNotifierSource<Data, Notification, NotificationCreator>, RootUnicastNotifier<Data, Notification, NotificationCreator>, RootUnicastNotifierCreator<Data, Notification, NotificationCreator>, RootUnicastNotifierDisposer<Data, Notification, NotificationCreator>> notifierPool;
	
	/*
	 * Initialize the resource pool for the notifier. 05/04/2014, Bing Li
	 */
	public ClusterRootUnicastNotifier(FreeClientPool clientPool, int poolSize, long resourceWaitTime, NotificationCreator creator)
	{
		this.clientPool = clientPool;
		
		this.creator = creator;
		
		this.notifierPool = new ResourcePool<RootUnicastNotifierSource<Data, Notification, NotificationCreator>, RootUnicastNotifier<Data, Notification, NotificationCreator>, RootUnicastNotifierCreator<Data, Notification, NotificationCreator>, RootUnicastNotifierDisposer<Data, Notification, NotificationCreator>>(poolSize, new RootUnicastNotifierCreator<Data, Notification, NotificationCreator>(), new RootUnicastNotifierDisposer<Data, Notification, NotificationCreator>(), resourceWaitTime);
	}

	/*
	 * Dispose the resource pool for the notifier. 05/04/2017, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.notifierPool.shutdown();
	}

	/*
	 * Disseminate the notification to one of the distributed nodes within the cluster. 05/04/2017, Bing Li
	 */
	public void notifiy(Data data, int rootBranchCount, int subBranchCount, String dnKey) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		RootUnicastNotifier<Data, Notification, NotificationCreator> notifier = this.notifierPool.get(new RootUnicastNotifierSource<Data, Notification, NotificationCreator>(this.clientPool, rootBranchCount, subBranchCount, this.creator));
		if (notifier != null)
		{
			notifier.disseminate(data, dnKey);
			this.notifierPool.collect(notifier);
		}
	}
	
	/*
	 * Disseminate the notification to a nearest one of the distributed nodes within the cluster in terms of ID string closeness. 05/04/2017, Bing Li
	 */
	public void notifyNearestly(Data data, String dataKey, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException
	{
		RootUnicastNotifier<Data, Notification, NotificationCreator> notifier = this.notifierPool.get(new RootUnicastNotifierSource<Data, Notification, NotificationCreator>(this.clientPool, rootBranchCount, subBranchCount, this.creator));
		if (notifier != null)
		{		
			notifier.nearestDisseminate(dataKey, data);
			this.notifierPool.collect(notifier);
		}
	}

	/*
	 * Disseminate the notification to a random one of the distributed nodes within the cluster. 05/04/2017, Bing Li
	 */
	public void notifiy(Data data, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		RootUnicastNotifier<Data, Notification, NotificationCreator> notifier = this.notifierPool.get(new RootUnicastNotifierSource<Data, Notification, NotificationCreator>(this.clientPool, rootBranchCount, subBranchCount, this.creator));
		if (notifier != null)
		{
			notifier.randomDisseminate(data);
			this.notifierPool.collect(notifier);
		}
	}

}
