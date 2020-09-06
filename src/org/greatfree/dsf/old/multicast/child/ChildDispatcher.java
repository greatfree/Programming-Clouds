package org.greatfree.dsf.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.MessageDisposer;
import org.greatfree.dsf.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

/*
 * As a parent dispatcher to keep fundamental multicasting for the cluster on the system level, the dispatcher needs to be derived by all of child dispatchers. 07/17/2017, Bing Li
 */

// Created: 07/17/2017, Bing Li
//public abstract class ChildDispatcher extends ServerDispatcher
public abstract class ChildDispatcher<Message extends ServerMessage> extends ServerDispatcher<Message>
{
	// The disposer is the binder that synchronizes the two bound notification dispatchers, processRootIPBroadcastNotificationDispatcher and broadcastRootIPNotificationDispatcher. After both of them finish their respective tasks concurrently, it disposes the notification of RootIPAddressBroadcastNotification finally. 11/27/2014, Bing Li
	private MessageDisposer<OldRootIPAddressBroadcastNotification> rootIPNotificationDisposer;
	// The dispatcher processes the notification of RootIPAddressBroadcastNotification. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, SubstrateRootIPAddressBroadcastNotificationThread, SubstrateRootIPAddressBroadcastNotificationThreadCreator> processRootIPBroadcastNotificationDispatcher;
	// The dispatcher to disseminate the notification of RootIPAddressBroadcastNotification to children nodes. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, SubstrateBroadcastRootIPAddressNotificationThread, SubstrateBroadcastRootIPAddressNotificationThreadCreator> broadcastRootIPNotificationDispatcher;

//	public ChildDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ChildDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the disposer for the notification of RootIPAddressBroadcastNotification, which works as a binder. 11/27/2014, Bing Li
		this.rootIPNotificationDisposer = new MessageDisposer<OldRootIPAddressBroadcastNotification>();

		// Initialize the bound notification dispatcher for the notification, RootIPAddressBroadcastNotification. 11/27/2014, Bing Li
		this.processRootIPBroadcastNotificationDispatcher = new BoundNotificationDispatcher.BoundNotificationDispatcherBuilder<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, SubstrateRootIPAddressBroadcastNotificationThread, SubstrateRootIPAddressBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.binder(this.rootIPNotificationDisposer)
				.threadCreator(new SubstrateRootIPAddressBroadcastNotificationThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		// Initialize the bound notification dispatcher for the notification, RootIPAddressBroadcastNotification, to disseminate the notification to children crawlers. 11/27/2014, Bing Li
		this.broadcastRootIPNotificationDispatcher = new BoundNotificationDispatcher.BoundNotificationDispatcherBuilder<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, SubstrateBroadcastRootIPAddressNotificationThread, SubstrateBroadcastRootIPAddressNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.binder(this.rootIPNotificationDisposer)
				.threadCreator(new SubstrateBroadcastRootIPAddressNotificationThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
	}

	/*
	 * Shutdown the dispatcher. 11/25/2014, Bing Li
	 */
	public void shutdownChild(long timeout) throws InterruptedException
	{
		// Shutdown the dispatcher. 05/20/2017, Bing Li
		super.shutdown(timeout);
		// Shutdown the bound notification dispatcher to process broadcast notifications. 11/27/2014, Bing Li
		this.processRootIPBroadcastNotificationDispatcher.dispose();
		// Shutdown the bound notification dispatcher for disseminating the broadcast notifications. 11/27/2014, Bing Li
		this.broadcastRootIPNotificationDispatcher.dispose();
	}
	
	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/25/2014, Bing Li
	 */
	public void broadcastRootIP(OldRootIPAddressBroadcastNotification rootIPNotification)
	{
//		System.out.println("<" + ClusterChildSingleton.CLUSTER().getChildIP() + ":" + ClusterChildSingleton.CLUSTER().getChildPort() + "> ROOT_IPADDRESS_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
		// Cast the message. 11/27/2014, Bing Li
//		rootIPNotification = (RootIPAddressBroadcastNotification)message.getMessage();
		// Check whether the bound broadcast notification dispatcher is ready. 02/02/2016, Bing Li
		if (!this.processRootIPBroadcastNotificationDispatcher.isReady())
		{
			// Execute the bound broadcast notification dispatcher. 02/02/2016, Bing Li
			super.execute(this.processRootIPBroadcastNotificationDispatcher);
		}
		// Enqueue the notification into those bound broadcast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
		this.processRootIPBroadcastNotificationDispatcher.enqueue(rootIPNotification);
		
		// Check whether the bound broadcast notification dispatcher is ready. 02/02/2016, Bing Li
		if (!this.broadcastRootIPNotificationDispatcher.isReady())
		{
			// Execute the bound notification broadcast dispatcher. 02/02/2016, Bing Li
			super.execute(this.broadcastRootIPNotificationDispatcher);
		}
		// Enqueue the notification into those bound broadcast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
		this.broadcastRootIPNotificationDispatcher.enqueue(rootIPNotification);
	}

//	public abstract void establishSubstrate(RootIPAddressBroadcastNotification rootIPNotification);
	
//	public abstract void removeSubstrate();
	
}
