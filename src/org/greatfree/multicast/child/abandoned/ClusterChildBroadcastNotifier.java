package org.greatfree.multicast.child.abandoned;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.reuse.ResourcePool;

/*
 * The class is employed by the CSCluster, the intermediate node in a cluster, to send broadcast notifications to each node within the sub cluster. 05/04/2017, Bing Li
 */

// Created: 05/05/2017, Bing Li
public class ClusterChildBroadcastNotifier<Notification extends OldMulticastMessage, NotificationCreator extends ChildBroadcastNotificationCreatable<Notification>>
{
	// The TCP client pool that sends notifications. 05/04/2017, Bing Li
	private FreeClientPool clientPool;

	// The notification creator. 05/04/2017, Bing Li
	private NotificationCreator creator;
	
	// The children TCP port. 05/05/2017, Bing Li
//	private int port;
	
	// The pool for the multicastor which broadcasts the notification. 05/04/2017, Bing Li
	private ResourcePool<ChildBroadcastNotifierSource<Notification, NotificationCreator>, ChildBroadcastNotifier<Notification, NotificationCreator>, ChildBroadcastNotifierCreator<Notification, NotificationCreator>, ChildBroadcastNotifierDisposer<Notification, NotificationCreator>> notifierPool;

	// The local IP key. The key is used to avoid the local node sends messages to itself. 05/19/2017, Bing Li
	private final String localIPKey;

	/*
	 * Initialize the resource pool for the notifier. 05/04/2014, Bing Li
	 */
//	public ClusterSubBroadcastNotifier(FreeClientPool clientPool, int port, int poolSize, long notifierAssignWaitTime, NotificationCreator creator)
//	public ClusterChildBroadcastNotifier(FreeClientPool clientPool, int poolSize, long notifierAssignWaitTime, NotificationCreator creator)
	public ClusterChildBroadcastNotifier(String localIPKey, FreeClientPool clientPool, int poolSize, long notifierAssignWaitTime, NotificationCreator creator)
	{
		this.localIPKey = localIPKey;
		this.clientPool = clientPool;
		
//		this.port = port;
		
		this.creator = creator;
		
		this.notifierPool = new ResourcePool<ChildBroadcastNotifierSource<Notification, NotificationCreator>, ChildBroadcastNotifier<Notification, NotificationCreator>, ChildBroadcastNotifierCreator<Notification, NotificationCreator>, ChildBroadcastNotifierDisposer<Notification, NotificationCreator>>(poolSize, new ChildBroadcastNotifierCreator<Notification, NotificationCreator>(), new ChildBroadcastNotifierDisposer<Notification, NotificationCreator>(), notifierAssignWaitTime);
	}

	/*
	 * Dispose the resource pool for the notifier. 05/04/2017, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.notifierPool.shutdown();
	}

	/*
	 * Disseminate the notification to all of the distributed nodes within the sub cluster. 05/04/2017, Bing Li
	 */
	public void notifiy(Notification notification, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
//		SubBroadcastNotifier<Notification, NotificationCreator> notifier = this.notifierPool.get(new SubBroadcastNotifierSource<Notification, NotificationCreator>(this.clientPool, subBranchCount, this.port, this.creator));
//		ChildBroadcastNotifier<Notification, NotificationCreator> notifier = this.notifierPool.get(new ChildBroadcastNotifierSource<Notification, NotificationCreator>(this.clientPool, subBranchCount, this.creator));
		ChildBroadcastNotifier<Notification, NotificationCreator> notifier = this.notifierPool.get(new ChildBroadcastNotifierSource<Notification, NotificationCreator>(this.localIPKey, this.clientPool, subBranchCount, this.creator));
		if (notifier != null)
		{
			notifier.disseminate(notification);
			this.notifierPool.collect(notifier);
		}
	}
}
