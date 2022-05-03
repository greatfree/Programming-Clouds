package org.greatfree.cluster.root;

import java.util.Calendar;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ChildResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.ClusterRequestStream;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;

// Created: 09/23/2018, Bing Li
class RootDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<JoinNotification, JoinNotificationThread, JoinNotificationThreadCreator> joinNotificationDispatcher;
	private NotificationDispatcher<LeaveNotification, LeaveNotificationThread, LeaveNotificationThreadCreator> leaveNotificationDispatcher;
//	private NotificationDispatcher<StopServerOnClusterNotification, StopServerOnClusterThread, StopServerOnClusterThreadCreator> stopServerNotificationDispatcher;

	private NotificationDispatcher<ClusterNotification, RootNotificationThread, RootNotificationThreadCreator> notificationDispatcher;
	private RequestDispatcher<ClusterRequest, ClusterRequestStream, CollectedClusterResponse, RootRequestThread, RootRequestThreadCreator> requestDispatcher;

	private NotificationDispatcher<ChildResponse, ChildResponseThread, ChildResponseThreadCreator> multicastResponseDispatcher;

//	public RootDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public RootDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		this.joinNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<JoinNotification, JoinNotificationThread, JoinNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new JoinNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.leaveNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<LeaveNotification, LeaveNotificationThread, LeaveNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new LeaveNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		/*
		this.stopServerNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<StopServerOnClusterNotification, StopServerOnClusterThread, StopServerOnClusterThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new StopServerOnClusterThreadCreator())
				.maxTaskSize(ServerConfig.MAX_NOTIFICATION_TASK_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
				*/

		this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ClusterNotification, RootNotificationThread, RootNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new RootNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClusterRequest, ClusterRequestStream, CollectedClusterResponse, RootRequestThread, RootRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new RootRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
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
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.joinNotificationDispatcher.dispose();
		this.leaveNotificationDispatcher.dispose();
//		this.stopServerNotificationDispatcher.dispose();
		this.notificationDispatcher.dispose();
		this.requestDispatcher.dispose();
		this.multicastResponseDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case ClusterMessageType.JOIN_NOTIFICATION:
				System.out.println("JOIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.joinNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.joinNotificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.joinNotificationDispatcher.enqueue((JoinNotification)message.getMessage());
				break;

			case ClusterMessageType.LEAVE_NOTIFICATION:
				System.out.println("LEAVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.leaveNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.leaveNotificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.leaveNotificationDispatcher.enqueue((LeaveNotification)message.getMessage());
				break;

				/*
			case ClusterMessageType.STOP_SERVER_ON_CLUSTER_NOTIFICATION:
				System.out.println("STOP_SERVER_ON_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.stopServerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.stopServerNotificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.stopServerNotificationDispatcher.enqueue((StopChatServerOnClusterNotification)message.getMessage());
				break;
				*/

			case MulticastMessageType.CLUSTER_NOTIFICATION:
				System.out.println("CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.notificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.notificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.notificationDispatcher.enqueue((ClusterNotification)message.getMessage());
				break;

			case MulticastMessageType.CLUSTER_REQUEST:
				System.out.println("CLUSTER_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.requestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.requestDispatcher);
				}
				// Enqueue the instance of Request into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.requestDispatcher.enqueue(new ClusterRequestStream(message.getOutStream(), message.getLock(), (ClusterRequest)message.getMessage()));
				break;

			case ClusterMessageType.CHILD_RESPONSE:
				System.out.println("CHILD_RESPONSE received @" + Calendar.getInstance().getTime());
				if (!this.multicastResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.multicastResponseDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.multicastResponseDispatcher.enqueue((ChildResponse)message.getMessage());
				break;
		}
		
	}

}
