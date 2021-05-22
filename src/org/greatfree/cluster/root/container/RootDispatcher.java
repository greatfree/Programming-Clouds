package org.greatfree.cluster.root.container;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.client.MessageStream;
import org.greatfree.cluster.message.AdditionalChildrenRequest;
import org.greatfree.cluster.message.AdditionalChildrenResponse;
import org.greatfree.cluster.message.AdditionalChildrenStream;
import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.cluster.message.ClusterSizeRequest;
import org.greatfree.cluster.message.ClusterSizeResponse;
import org.greatfree.cluster.message.ClusterSizeStream;
import org.greatfree.cluster.message.HeavyWorkloadNotification;
import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.cluster.message.PartitionSizeRequest;
import org.greatfree.cluster.message.PartitionSizeResponse;
import org.greatfree.cluster.message.PartitionSizeStream;
import org.greatfree.cluster.message.SuperfluousResourcesNotification;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ChildResponse;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ChildRootStream;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.IntercastRequestStream;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.RequestStream;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.server.ServerDispatcher;

// Created: 01/13/2019, Bing Li
class RootDispatcher extends ServerDispatcher<ServerMessage>
{
	/*
	 * The below messages are in the system level ones which are processed internally such that programmers do not need to care. 09/12/2020, Bing Li
	 */
	private NotificationDispatcher<JoinNotification, JoinNotificationThread, JoinNotificationThreadCreator> joinNotificationDispatcher;
	private NotificationDispatcher<LeaveNotification, LeaveNotificationThread, LeaveNotificationThreadCreator> leaveNotificationDispatcher;
	private NotificationDispatcher<HeavyWorkloadNotification, HeavyWorkloadNotificationThread, HeavyWorkloadNotificationThreadCreator> heavyWorkloadNotificationDispatcher;
	private NotificationDispatcher<SuperfluousResourcesNotification, SuperfluousResourcesNotificationThread, SuperfluousResourcesNotificationThreadCreator> superfluousResourcesNotificationDispatcher;
	private RequestDispatcher<PartitionSizeRequest, PartitionSizeStream, PartitionSizeResponse, PartitionSizeRequestThread, PartitionSizeRequestThreadCreator> partitionSizeRequestDispatcher;
	private RequestDispatcher<ClusterSizeRequest, ClusterSizeStream, ClusterSizeResponse, ClusterSizeRequestThread, ClusterSizeRequestThreadCreator> clusterSizeRequestDispatcher;
	private RequestDispatcher<AdditionalChildrenRequest, AdditionalChildrenStream, AdditionalChildrenResponse, AdditionalChildrenRequestThread, AdditionalChildrenRequestThreadCreator> additionalChildrenRequestDispatcher;
	
	/*
	 * The below messages are interchanged from the child to the root. They belong to the upper level applications. So the approaches to process them are defined by the RootTask. 09/14/2020, Bing Li
	 */
	private RequestDispatcher<ChildRootRequest, ChildRootStream, ChildRootResponse, ChildRootRequestThread, ChildRootRequestThreadCreator> childRootRequestDispatcher;

	/*
	 * These messages are in the application level ones, which should be processed as tasks which programmers need to take care of. 09/12/2020, Bing Li
	 */
	private NotificationDispatcher<Notification, RootNotificationThread, RootNotificationThreadCreator> notificationDispatcher;
	private RequestDispatcher<Request, RequestStream, Response, RootRequestThread, RootRequestThreadCreator> requestDispatcher;
	
	private NotificationDispatcher<IntercastNotification, IntercastNotificationThread, IntercastNotificationThreadCreator> intercastNotificationDispatcher;
	private RequestDispatcher<IntercastRequest, IntercastRequestStream, Response, IntercastRequestThread, IntercastRequestThreadCreator> intercastRequestDispatcher;

	private NotificationDispatcher<ChildResponse, ChildResponseThread, ChildResponseThreadCreator> multicastResponseDispatcher;

	private final static Logger log = Logger.getLogger("org.greatfree.cluster.child.container");

//	public RootDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public RootDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
//		super(schedulerPoolSize, schedulerKeepAliveTime);
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

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

		this.heavyWorkloadNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HeavyWorkloadNotification, HeavyWorkloadNotificationThread, HeavyWorkloadNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HeavyWorkloadNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.superfluousResourcesNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SuperfluousResourcesNotification, SuperfluousResourcesNotificationThread, SuperfluousResourcesNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SuperfluousResourcesNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.partitionSizeRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PartitionSizeRequest, PartitionSizeStream, PartitionSizeResponse, PartitionSizeRequestThread, PartitionSizeRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PartitionSizeRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.clusterSizeRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClusterSizeRequest, ClusterSizeStream, ClusterSizeResponse, ClusterSizeRequestThread, ClusterSizeRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new ClusterSizeRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.additionalChildrenRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<AdditionalChildrenRequest, AdditionalChildrenStream, AdditionalChildrenResponse, AdditionalChildrenRequestThread, AdditionalChildrenRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new AdditionalChildrenRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.childRootRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ChildRootRequest, ChildRootStream, ChildRootResponse, ChildRootRequestThread, ChildRootRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new ChildRootRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<Notification, RootNotificationThread, RootNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new RootNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<Request, RequestStream, Response, RootRequestThread, RootRequestThreadCreator>()
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

		this.intercastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<IntercastRequest, IntercastRequestStream, Response, IntercastRequestThread, IntercastRequestThreadCreator>()
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
		this.joinNotificationDispatcher.dispose();
		this.leaveNotificationDispatcher.dispose();
		this.heavyWorkloadNotificationDispatcher.dispose();
		this.superfluousResourcesNotificationDispatcher.dispose();
		this.partitionSizeRequestDispatcher.dispose();
		this.clusterSizeRequestDispatcher.dispose();
		this.additionalChildrenRequestDispatcher.dispose();
		this.childRootRequestDispatcher.dispose();
		this.notificationDispatcher.dispose();
		this.requestDispatcher.dispose();
		this.multicastResponseDispatcher.dispose();
		this.intercastNotificationDispatcher.dispose();
		this.intercastRequestDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case ClusterMessageType.JOIN_NOTIFICATION:
				log.info("JOIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.joinNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.joinNotificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.joinNotificationDispatcher.enqueue((JoinNotification)message.getMessage());
				break;

			case ClusterMessageType.LEAVE_NOTIFICATION:
				log.info("LEAVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.leaveNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.leaveNotificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.leaveNotificationDispatcher.enqueue((LeaveNotification)message.getMessage());
				break;
				
			case ClusterMessageType.HEAVY_WORKLOAD_NOTIFICATION:
				log.info("HEAVY_WORKLOAD_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.heavyWorkloadNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.heavyWorkloadNotificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.heavyWorkloadNotificationDispatcher.enqueue((HeavyWorkloadNotification)message.getMessage());
				break;
				
			case ClusterMessageType.SUPERFLUOUS_RESOURCES_NOTIFICATION:
				log.info("SUPERFLUOUS_RESOURCES_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.superfluousResourcesNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.superfluousResourcesNotificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.superfluousResourcesNotificationDispatcher.enqueue((SuperfluousResourcesNotification)message.getMessage());
				break;
				
			case ClusterMessageType.PARTITION_SIZE_REQUEST:
				log.info("PARTITION_SIZE_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.partitionSizeRequestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.partitionSizeRequestDispatcher);
				}
				// Enqueue the instance of Request into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.partitionSizeRequestDispatcher.enqueue(new PartitionSizeStream(message.getOutStream(), message.getLock(), (PartitionSizeRequest)message.getMessage()));
				break;
				
			case ClusterMessageType.CLUSTER_SIZE_REQUEST:
				log.info("CLUSTER_SIZE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.clusterSizeRequestDispatcher.isReady())
				{
					super.execute(this.clusterSizeRequestDispatcher);
				}
				this.clusterSizeRequestDispatcher.enqueue(new ClusterSizeStream(message.getOutStream(), message.getLock(), (ClusterSizeRequest)message.getMessage()));
				break;
				
			case ClusterMessageType.ADDITIONAL_CHILDREN_REQUEST:
				log.info("ADDITIONAL_CHILDREN_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.additionalChildrenRequestDispatcher.isReady())
				{
					super.execute(this.additionalChildrenRequestDispatcher);
				}
				this.additionalChildrenRequestDispatcher.enqueue(new AdditionalChildrenStream(message.getOutStream(), message.getLock(), (AdditionalChildrenRequest)message.getMessage()));
				break;
				
			case MulticastMessageType.CHILD_ROOT_REQUEST:
				log.info("CHILD_ROOT_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.childRootRequestDispatcher.isReady())
				{
					super.execute(this.childRootRequestDispatcher);
				}
				this.childRootRequestDispatcher.enqueue(new ChildRootStream(message.getOutStream(), message.getLock(), (ChildRootRequest)message.getMessage()));
				break;

			case MulticastMessageType.NOTIFICATION:
				log.info("NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.notificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.notificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.notificationDispatcher.enqueue((Notification)message.getMessage());
				break;

			case MulticastMessageType.REQUEST:
				log.info("REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.requestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.requestDispatcher);
				}
				// Enqueue the instance of Request into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.requestDispatcher.enqueue(new RequestStream(message.getOutStream(), message.getLock(), (Request)message.getMessage()));
				break;

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
