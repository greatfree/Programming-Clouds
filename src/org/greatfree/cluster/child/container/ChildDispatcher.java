package org.greatfree.cluster.child.container;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ChildResponse;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.IntercastRequestStream;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.message.multicast.container.RootAddressNotification;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;

// Created: 01/13/2019, Bing Li
class ChildDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<RootAddressNotification, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator> rootIPBroadcastNotificationDispatcher;
	
	// Since the message, SelectedChildNotification, needs to be forwarded/broadcast, it inherits Notification. The message is processed in the thread,  ChildNotificationThread. So the below lines is NOT useful. 09/12/2020, Bing Li
//	private NotificationDispatcher<SelectedChildNotification, SelectedChildNotificationThread, SelectedChildNotificationThreadCreator> selectedChildNotificationDispatcher;

	private NotificationDispatcher<ClusterNotification, ChildNotificationThread, ChildNotificationThreadCreator> notificationDispatcher;
	private NotificationDispatcher<ClusterRequest, ChildRequestThread, ChildRequestThreadCreator> requestDispatcher;
	
	private NotificationDispatcher<ChildResponse, ChildResponseThread, ChildResponseThreadCreator> multicastResponseDispatcher;
	
	private NotificationDispatcher<IntercastNotification, IntercastNotificationThread, IntercastNotificationThreadCreator> intercastNotificationDispatcher;
	private RequestDispatcher<IntercastRequest, IntercastRequestStream, CollectedClusterResponse, IntercastRequestThread, IntercastRequestThreadCreator> intercastRequestDispatcher;

	private final static Logger log = Logger.getLogger("org.greatfree.cluster.child.container");

	/*
	 * I am implementing the root-based intercasting. It seems that the below lines are not necessary temporarily. 02/15/2019, Bing Li
	 */
	/*
	 * The below problem is solved. The child task can be written in the same way. 02/10/2019, Bing Li
	 * 
	 * If the intercast messages should be processed through direct TCP connection in the below way, how to write the ChildTask? 02/09/2019, Bing Li
	 */
//	private NotificationDispatcher<IntercastNotification, IntercastNotificationThread, IntercastNotificationThreadCreator> intercastNotificationDispatcher;
//	private RequestDispatcher<IntercastRequest, IntercastRequestStream, ServerMessage, IntercastRequestThread, IntercastRequestThreadCreator> intercastRequestDispatcher;

//	public ChildDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ChildDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
//		super(schedulerPoolSize, schedulerKeepAliveTime);
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, ChatNotification
		this.rootIPBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RootAddressNotification, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new RootIPAddressBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		// Since the message, SelectedChildNotification, needs to be forwarded/broadcast, it inherits Notification. So the below lines is NOT useful. 09/12/2020, Bing Li
		/*
		this.selectedChildNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SelectedChildNotification, SelectedChildNotificationThread, SelectedChildNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SelectedChildNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
				*/

		this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ClusterNotification, ChildNotificationThread, ChildNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ChildNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.requestDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ClusterRequest, ChildRequestThread, ChildRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ChildRequestThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		/*
		 * I am implementing the root-based intercasting. It seems that the below lines are not necessary temporarily. 02/15/2019, Bing Li
		 */
		/*
		this.intercastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<IntercastNotification, IntercastNotificationThread, IntercastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new IntercastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.intercastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<IntercastRequest, IntercastRequestStream, ServerMessage, IntercastRequestThread, IntercastRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new IntercastRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
				*/
		
		this.multicastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ChildResponse, ChildResponseThread, ChildResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ChildResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.intercastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<IntercastNotification, IntercastNotificationThread, IntercastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new IntercastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.intercastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<IntercastRequest, IntercastRequestStream, CollectedClusterResponse, IntercastRequestThread, IntercastRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new IntercastRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.rootIPBroadcastNotificationDispatcher.dispose();
		// Since the message, SelectedChildNotification, needs to be forwarded/broadcast, it inherits Notification. The message is processed in the thread,  ChildNotificationThread. So the below lines is NOT useful. 09/12/2020, Bing Li
//		this.selectedChildNotificationDispatcher.dispose();
		this.notificationDispatcher.dispose();
		this.requestDispatcher.dispose();
//		this.intercastNotificationDispatcher.dispose();
//		this.intercastRequestDispatcher.dispose();
		this.multicastResponseDispatcher.dispose();
		this.intercastNotificationDispatcher.dispose();
		this.intercastRequestDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION:
				log.info("ROOT_IPADDRESS_BROADCAST_NOTIFICATION received at " + Calendar.getInstance().getTime());
				if (!this.rootIPBroadcastNotificationDispatcher.isReady())
				{
					super.execute(this.rootIPBroadcastNotificationDispatcher);
				}
				this.rootIPBroadcastNotificationDispatcher.enqueue((RootAddressNotification)message.getMessage());
				break;

				// Since the message, SelectedChildNotification, needs to be forwarded/broadcast, it inherits Notification. The message is processed in the thread,  ChildNotificationThread. So the below lines is NOT useful. 09/12/2020, Bing Li
				/*
			case ClusterMessageType.SELECTED_CHILD_NOTIFICATION:
				log.info("SELECTED_CHILD_NOTIFICATION received at " + Calendar.getInstance().getTime());
				if (!this.selectedChildNotificationDispatcher.isReady())
				{
					super.execute(this.selectedChildNotificationDispatcher);
				}
				this.selectedChildNotificationDispatcher.enqueue((SelectedChildNotification)message.getMessage());
				break;
				*/
				
			case MulticastMessageType.CLUSTER_NOTIFICATION:
				log.info("CLUSTER_NOTIFICATION received at " + Calendar.getInstance().getTime());
				if (!this.notificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.notificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.notificationDispatcher.enqueue((ClusterNotification)message.getMessage());
				break;
				
			case MulticastMessageType.CLUSTER_REQUEST:
				log.info("CLUSTER_REQUEST received at " + Calendar.getInstance().getTime());
				if (!this.requestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.requestDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.requestDispatcher.enqueue((ClusterRequest)message.getMessage());
				break;

				/*
			case MulticastMessageType.INTERCAST_NOTIFICATION:
				log.info("INTERCAST_NOTIFICATION received at " + Calendar.getInstance().getTime());
				if (!this.intercastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.intercastNotificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.intercastNotificationDispatcher.enqueue((IntercastNotification)message.getMessage());
				break;
				
			case MulticastMessageType.INTERCAST_REQUEST:
				log.info("INTERCAST_REQUEST received at " + Calendar.getInstance().getTime());
				if (!this.intercastRequestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.intercastRequestDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.intercastRequestDispatcher.enqueue(new IntercastRequestStream(message.getOutStream(), message.getLock(), (IntercastRequest)message.getMessage()));
				break;
				*/

			case ClusterMessageType.CHILD_RESPONSE:
				log.info("CHILD_RESPONSE received @" + Calendar.getInstance().getTime());
				if (!this.multicastResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.multicastResponseDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.multicastResponseDispatcher.enqueue((ChildResponse)message.getMessage());
				break;
				
			case MulticastMessageType.INTERCAST_NOTIFICATION:
				log.info("INTERCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.intercastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.intercastNotificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.intercastNotificationDispatcher.enqueue((IntercastNotification)message.getMessage());
				break;
				
			case MulticastMessageType.INTERCAST_REQUEST:
				log.info("INTERCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.intercastRequestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.intercastRequestDispatcher);
				}
				// Enqueue the instance of Request into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.intercastRequestDispatcher.enqueue(new IntercastRequestStream(message.getOutStream(), message.getLock(), (IntercastRequest)message.getMessage()));
				break;
		}
	}

}
