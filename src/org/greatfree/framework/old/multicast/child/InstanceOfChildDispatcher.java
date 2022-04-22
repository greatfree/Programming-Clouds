package org.greatfree.framework.old.multicast.child;

import java.util.Calendar;

import org.greatfree.concurrency.reactive.BoundNotificationDispatcher;
import org.greatfree.concurrency.reactive.BoundRequestDispatcher;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;
import org.greatfree.framework.multicast.message.HelloWorldAnycastRequest;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;
import org.greatfree.framework.multicast.message.HelloWorldUnicastRequest;
import org.greatfree.framework.multicast.message.MulticastDIPMessageType;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastNotification;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastRequest;
import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.framework.multicast.message.OldShutdownChildrenBroadcastNotification;
import org.greatfree.message.MessageDisposer;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;

/*
 * The dispatcher serves as a message dispatcher for messages within the cluster. 05/10/2017, Bing Li
 */

// Created: 05/11/2017, Bing Li
//public class InstanceChildDispatcher extends ChildDispatcher
class InstanceOfChildDispatcher extends ServerDispatcher<ServerMessage>
{

	// The disposer is the binder that synchronizes the two bound notification dispatchers, processRootIPBroadcastNotificationDispatcher and broadcastRootIPNotificationDispatcher. After both of them finish their respective tasks concurrently, it disposes the notification of RootIPAddressBroadcastNotification finally. 11/27/2014, Bing Li
	private MessageDisposer<OldRootIPAddressBroadcastNotification> rootIPNotificationDisposer;
	// The dispatcher processes the notification of RootIPAddressBroadcastNotification. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator> processRootIPBroadcastNotificationDispatcher;
	// The dispatcher to disseminate the notification of RootIPAddressBroadcastNotification to children nodes. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, BroadcastRootIPAddressNotificationThread, BroadcastRootIPAddressNotificationThreadCreator> broadcastRootIPNotificationDispatcher;

	// The disposer is the binder that synchronizes the two bound notification dispatchers, processHelloBroadcastNotificationDispatcher and broadcastHelloWorldNotificationDispatcher. After both of them finish their respective tasks concurrently, it disposes the notification of HelloWorldNotification finally. 11/27/2014, Bing Li
	private MessageDisposer<OldHelloWorldBroadcastNotification> helloNotificationDisposer;
	// The dispatcher processes the notification of HelloWorldBroadcastNotification. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>, HelloWorldBroadcastNotificationThread, HelloWorldBroadcastNotificationThreadCreator> processHelloBroadcastNotificationDispatcher;
	// The dispatcher to disseminate the notification of HelloWorldBroadcastNotification to children nodes. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>, BroadcastHelloWorldNotificationThread, BroadcastHelloWorldNotificationThreadCreator> broadcastHelloWorldNotificationDispatcher;

	// Declare a notification dispatcher to process anycast messages to the child when such a notification is received. 05/19/2016, Bing Li
	private NotificationDispatcher<HelloWorldAnycastNotification, HelloWorldAnycastNotificationThread, HelloWorldAnycastNotificationThreadCreator> processHelloWorldAnycastNotificationDispatcher;

	// Declare a notification dispatcher to process unicast messages to the child when such a notification is received. 05/19/2016, Bing Li
	private NotificationDispatcher<HelloWorldUnicastNotification, HelloWorldUnicastNotificationThread, HelloWorldUnicastNotificationThreadCreator> processHelloWorldUnicastNotificationDispatcher;
	
	// The disposer is the binder that synchronizes the two bound notification dispatchers, broadcastNotificationDispatcher and multicastBroadcastNotificationDispatcher. After both of them finish their respective tasks concurrently, it disposes the notification of ShutdownChildrenBroadcastNotification finally. 11/27/2014, Bing Li
//	private ClusterMessageDisposer<ShutdownChildrenBroadcastNotification> shutdownDisposer;
	// The dispatcher processes the notification of ShutdownChildrenBroadcastNotification. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
//	private BoundNotificationDispatcher<ShutdownChildrenBroadcastNotification, ClusterMessageDisposer<ShutdownChildrenBroadcastNotification>, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator> processShutdownBroadcastNotificationDispatcher;
	// The dispatcher to disseminate the notification of ShutdownChildrenBroadcastNotification to children nodes. It must be synchronized by the binder, shutdownDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
//	private BoundNotificationDispatcher<ShutdownChildrenBroadcastNotification, ClusterMessageDisposer<ShutdownChildrenBroadcastNotification>, BroadcastShutdownNotificationThread, BroadcastShutdownNotificationThreadCreator> broadcastShutdownNotificationDispatcher;
	
	// Declare the message disposer to collect requests after it is handled and broadcast. 01/14/2016, Bing Li
	private MessageDisposer<OldHelloWorldBroadcastRequest> helloRequestDisposer;
	// Declare the dispatcher to handle the broadcast request. 01/14/2016, Bing Li 
	private BoundRequestDispatcher<OldHelloWorldBroadcastRequest, HelloWorldBroadcastResponse, MessageDisposer<OldHelloWorldBroadcastRequest>, HelloWorldBroadcastRequestThread, HelloWorldBroadcastRequestThreadCreator> processHelloWorldBroadcastRequestDispatcher;
	// Declare the broadcast dispatcher to broadcast the request. 01/14/2016, Bing Li
	private BoundNotificationDispatcher<OldHelloWorldBroadcastRequest, MessageDisposer<OldHelloWorldBroadcastRequest>, BroadcastHelloWorldRequestThread, BroadcastHelloWorldRequestThreadCreator> multicastBroadcastRequestDispatcher;
	
	// Declare a notification dispatcher to process anycast requests to the child when such a notification is received. 05/19/2016, Bing Li
	private NotificationDispatcher<HelloWorldAnycastRequest, HelloWorldAnycastRequestThread, HelloWorldAnycastRequestThreadCreator> processHelloWorldAnycastRequestDispatcher;

	// Declare a notification dispatcher to process unicast requests to the child when such a notification is received. 05/19/2016, Bing Li
	private NotificationDispatcher<HelloWorldUnicastRequest, HelloWorldUnicastRequestThread, HelloWorldUnicastRequestThreadCreator> processHelloWorldUnicastRequestDispatcher;

	// The dispatcher processes the notification of ShutdownChildrenBroadcastNotification. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private NotificationDispatcher<OldShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator> processShutdownBroadcastNotificationDispatcher;

	/*
	 * Initialize the dispatcher. 11/25/2014, Bing Li
	 */
//	public InstanceOfChildDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public InstanceOfChildDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);
		
		// Initialize the disposer for the notification of RootIPAddressBroadcastNotification, which works as a binder. 11/27/2014, Bing Li
		this.rootIPNotificationDisposer = new MessageDisposer<OldRootIPAddressBroadcastNotification>();

		// Initialize the bound notification dispatcher for the notification, RootIPAddressBroadcastNotification. 11/27/2014, Bing Li
		this.processRootIPBroadcastNotificationDispatcher = new BoundNotificationDispatcher.BoundNotificationDispatcherBuilder<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.binder(this.rootIPNotificationDisposer)
				.threadCreator(new RootIPAddressBroadcastNotificationThreadCreator())
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
		this.broadcastRootIPNotificationDispatcher = new BoundNotificationDispatcher.BoundNotificationDispatcherBuilder<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, BroadcastRootIPAddressNotificationThread, BroadcastRootIPAddressNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.binder(this.rootIPNotificationDisposer)
				.threadCreator(new BroadcastRootIPAddressNotificationThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		// Initialize the disposer for the notification of HelloWorldNotification, which works as a binder. 11/27/2014, Bing Li
		this.helloNotificationDisposer = new MessageDisposer<OldHelloWorldBroadcastNotification>();

		// Initialize the bound notification dispatcher for the notification, HelloWorldBroadcastNotification. 11/27/2014, Bing Li
		this.processHelloBroadcastNotificationDispatcher = new BoundNotificationDispatcher.BoundNotificationDispatcherBuilder<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>, HelloWorldBroadcastNotificationThread, HelloWorldBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.binder(this.helloNotificationDisposer)
				.threadCreator(new HelloWorldBroadcastNotificationThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		// Initialize the bound notification dispatcher for the notification, BroadcastNotification, to disseminate the notification to children crawlers. 11/27/2014, Bing Li
		this.broadcastHelloWorldNotificationDispatcher = new BoundNotificationDispatcher.BoundNotificationDispatcherBuilder<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>, BroadcastHelloWorldNotificationThread, BroadcastHelloWorldNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.binder(this.helloNotificationDisposer)
				.threadCreator(new BroadcastHelloWorldNotificationThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		// Initialize the notification dispatcher for the notification, HelloWorldAnycastNotification. 11/27/2014, Bing Li
		this.processHelloWorldAnycastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldAnycastNotification, HelloWorldAnycastNotificationThread, HelloWorldAnycastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new HelloWorldAnycastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		// Initialize the notification dispatcher for the notification, HelloWorldAnycastNotification. 11/27/2014, Bing Li
		this.processHelloWorldUnicastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldUnicastNotification, HelloWorldUnicastNotificationThread, HelloWorldUnicastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new HelloWorldUnicastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the disposer for the notification of ShutdownChildrenBroadcastNotification, which works as a binder. 11/27/2014, Bing Li
		/*
		this.shutdownDisposer = new ClusterMessageDisposer<ShutdownChildrenBroadcastNotification>();

		// Initialize the bound notification dispatcher for the notification, ShutdownChildrenBroadcastNotification. 11/27/2014, Bing Li
		this.processShutdownBroadcastNotificationDispatcher = new BoundNotificationDispatcher.BoundNotificationDispatcherBuilder<ShutdownChildrenBroadcastNotification, ClusterMessageDisposer<ShutdownChildrenBroadcastNotification>, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
				.binder(this.shutdownDisposer)
				.threadCreator(new ShutdownChildrenBroadcastNotificationThreadCreator())
				.maxTaskSize(ServerConfig.MAX_NOTIFICATION_TASK_SIZE)
				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getSchedulerPool())
				.build();
		
		// Initialize the bound notification dispatcher for the notification, ShutdownChildrenBroadcastNotification, to disseminate the notification to children crawlers. 11/27/2014, Bing Li
		this.broadcastShutdownNotificationDispatcher = new BoundNotificationDispatcher.BoundNotificationDispatcherBuilder<ShutdownChildrenBroadcastNotification, ClusterMessageDisposer<ShutdownChildrenBroadcastNotification>, BroadcastShutdownNotificationThread, BroadcastShutdownNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
				.binder(this.shutdownDisposer)
				.threadCreator(new BroadcastShutdownNotificationThreadCreator())
				.maxTaskSize(ServerConfig.MAX_NOTIFICATION_TASK_SIZE)
				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getSchedulerPool())
				.build();
				*/
		
		// Initialize the disposer for the notification of HelloWorldBroadcastRequest, which works as a binder. 11/27/2014, Bing Li
		this.helloRequestDisposer = new MessageDisposer<OldHelloWorldBroadcastRequest>();
		// Initialize the dispatcher to handle the request. 01/14/2016, Bing Li 
		this.processHelloWorldBroadcastRequestDispatcher = new BoundRequestDispatcher.BoundRequestDispatcherBuilder<OldHelloWorldBroadcastRequest, HelloWorldBroadcastResponse, MessageDisposer<OldHelloWorldBroadcastRequest>, HelloWorldBroadcastRequestThread, HelloWorldBroadcastRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.binder(this.helloRequestDisposer)
				.threadCreator(new HelloWorldBroadcastRequestThreadCreator())
				.maxTaskSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		// Initialize the dispatcher to broadcast the request. 05/20/2017, Bing Li
		this.multicastBroadcastRequestDispatcher = new BoundNotificationDispatcher.BoundNotificationDispatcherBuilder<OldHelloWorldBroadcastRequest, MessageDisposer<OldHelloWorldBroadcastRequest>, BroadcastHelloWorldRequestThread, BroadcastHelloWorldRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.binder(this.helloRequestDisposer)
				.threadCreator(new BroadcastHelloWorldRequestThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		
		// Initialize the notification dispatcher for the notification, HelloWorldAnycastRequest. 11/27/2014, Bing Li
		this.processHelloWorldAnycastRequestDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldAnycastRequest, HelloWorldAnycastRequestThread, HelloWorldAnycastRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new HelloWorldAnycastRequestThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		// Initialize the notification dispatcher for the notification, HelloWorldUnicastRequest. 11/27/2014, Bing Li
		this.processHelloWorldUnicastRequestDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldUnicastRequest, HelloWorldUnicastRequestThread, HelloWorldUnicastRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new HelloWorldUnicastRequestThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the notification dispatcher for the notification, HelloWorldAnycastNotification. 11/27/2014, Bing Li
		this.processShutdownBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<OldShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ShutdownChildrenBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
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
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// Shutdown the dispatcher. 05/20/2017, Bing Li
		super.shutdown(timeout);
		// Shutdown the bound notification dispatcher to process broadcast notifications. 11/27/2014, Bing Li
		this.processRootIPBroadcastNotificationDispatcher.dispose();
		// Shutdown the bound notification dispatcher for disseminating the broadcast notifications. 11/27/2014, Bing Li
		this.broadcastRootIPNotificationDispatcher.dispose();
		// Shutdown the bound notification dispatcher to process broadcast notifications. 11/27/2014, Bing Li
		this.processHelloBroadcastNotificationDispatcher.dispose();
		// Shutdown the bound notification dispatcher for disseminating the broadcast notifications. 11/27/2014, Bing Li
		this.broadcastHelloWorldNotificationDispatcher.dispose();
		// Shutdown the notification dispatcher to process anycast notifications. 11/27/2014, Bing Li
		this.processHelloWorldAnycastNotificationDispatcher.dispose();
		// Shutdown the notification dispatcher to process unicast notifications. 11/27/2014, Bing Li
		this.processHelloWorldUnicastNotificationDispatcher.dispose();
		// Shutdown the request dispatcher that handles the request. 05/21/2017, Bing Li
		this.processHelloWorldBroadcastRequestDispatcher.dispose();
		// Shutdown the request dispatcher that multicast the request. 05/21/2017, Bing Li
		this.multicastBroadcastRequestDispatcher.dispose();
		// Shutdown the bound notification dispatcher to process anycast requests. 11/27/2014, Bing Li
		this.processHelloWorldAnycastRequestDispatcher.dispose();
		// Shutdown the bound notification dispatcher to process unicast requests. 11/27/2014, Bing Li
		this.processHelloWorldUnicastRequestDispatcher.dispose();
		// Shutdown the bound notification dispatcher to process broadcast notifications. 11/27/2014, Bing Li
		this.processShutdownBroadcastNotificationDispatcher.dispose();
		// Shutdown the bound notification dispatcher for disseminating the broadcast notifications. 11/27/2014, Bing Li
//		this.broadcastShutdownNotificationDispatcher.dispose();
	}

	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/25/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		OldHelloWorldBroadcastNotification helloWorldNotification;
//		ShutdownChildrenBroadcastNotification shutdownNotification;
		OldRootIPAddressBroadcastNotification rootIPNotification;
		OldHelloWorldBroadcastRequest helloWorldRequest;
		switch (message.getMessage().getType())
		{
			case MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION:
				System.out.println("<" + ClusterChildSingleton.CLUSTER().getChildIP() + ":" + ClusterChildSingleton.CLUSTER().getChildPort() + "> ROOT_IPADDRESS_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Cast the message. 11/27/2014, Bing Li
				rootIPNotification = (OldRootIPAddressBroadcastNotification)message.getMessage();
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
//				super.consume((RootIPAddressBroadcastNotification)message.getMessage());
				break;
			
			// Process the notification of HelloWorldBroadcastNotification. 11/27/2014, Bing Li
			case MulticastDIPMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION:
				System.out.println("<" + ClusterChildSingleton.CLUSTER().getChildIP() + ":" + ClusterChildSingleton.CLUSTER().getChildPort() + ": HELLO_WORLD_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Cast the message. 11/27/2014, Bing Li
				helloWorldNotification = (OldHelloWorldBroadcastNotification)message.getMessage();
				// Check whether the bound broadcast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.processHelloBroadcastNotificationDispatcher.isReady())
				{
					// Execute the bound broadcast notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.processHelloBroadcastNotificationDispatcher);
				}
				// Enqueue the notification into those bound broadcast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.processHelloBroadcastNotificationDispatcher.enqueue(helloWorldNotification);
				
				// Check whether the bound broadcast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.broadcastHelloWorldNotificationDispatcher.isReady())
				{
					// Execute the bound notification broadcast dispatcher. 02/02/2016, Bing Li
					super.execute(this.broadcastHelloWorldNotificationDispatcher);
				}
				// Enqueue the notification into those bound broadcast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.broadcastHelloWorldNotificationDispatcher.enqueue(helloWorldNotification);
				break;
				
			case MulticastDIPMessageType.HELLO_WORLD_ANYCAST_NOTIFICATION:
				System.out.println("<" + ClusterChildSingleton.CLUSTER().getChildIP() + ":" + ClusterChildSingleton.CLUSTER().getChildPort() + ": HELLO_WORLD_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the anycast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.processHelloWorldAnycastNotificationDispatcher.isReady())
				{
					// Execute the anycast notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.processHelloWorldAnycastNotificationDispatcher);
				}
				// Enqueue the notification into those anycast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.processHelloWorldAnycastNotificationDispatcher.enqueue((HelloWorldAnycastNotification)message.getMessage());
				break;
				
			case MulticastDIPMessageType.HELLO_WORLD_UNICAST_NOTIFICATION:
				System.out.println("<" + ClusterChildSingleton.CLUSTER().getChildIP() + ":" + ClusterChildSingleton.CLUSTER().getChildPort() + ": HELLO_WORLD_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the unicast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.processHelloWorldUnicastNotificationDispatcher.isReady())
				{
					// Execute the unicast notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.processHelloWorldUnicastNotificationDispatcher);
				}
				// Enqueue the notification into those unicast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.processHelloWorldUnicastNotificationDispatcher.enqueue((HelloWorldUnicastNotification)message.getMessage());
				break;
				
			case MulticastDIPMessageType.HELLO_WORLD_BROADCAST_REQUEST:
				System.out.println("<" + ClusterChildSingleton.CLUSTER().getChildIP() + ":" + ClusterChildSingleton.CLUSTER().getChildPort() + ": HELLO_WORLD_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Get the broadcast request. 05/20/2017, Bing Li
				helloWorldRequest = (OldHelloWorldBroadcastRequest)message.getMessage();
				// Check whether the broadcast request processing dispatcher is ready. 05/21/2017, Bing Li
				if (!this.processHelloWorldBroadcastRequestDispatcher.isReady())
				{
					// Execute the broadcast request processing dispatcher. 05/21/2017, Bing Li
					super.execute(this.processHelloWorldBroadcastRequestDispatcher);
				}
				// Enqueue the broadcast request into the broadcast request processing dispatcher. 05/21/2017, Bing Li
				this.processHelloWorldBroadcastRequestDispatcher.enqueue(helloWorldRequest);

				// Check whether the broadcast request multicasting dispatcher is ready. 05/21/2017, Bing Li
				if (!this.multicastBroadcastRequestDispatcher.isReady())
				{
					// Execute the broadcast request multicasting dispatcher. 05/21/2017, Bing Li
					super.execute(this.multicastBroadcastRequestDispatcher);
				}
				// Enqueue the broadcast request into the multicasting dispatcher. 05/21/2017, Bing Li
				this.multicastBroadcastRequestDispatcher.enqueue(helloWorldRequest);
				
				break;

			case MulticastDIPMessageType.HELLO_WORLD_ANYCAST_REQUEST:
				System.out.println("<" + ClusterChildSingleton.CLUSTER().getChildIP() + ":" + ClusterChildSingleton.CLUSTER().getChildPort() + ": HELLO_WORLD_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the anycast request dispatcher is ready. 02/02/2016, Bing Li
				if (!this.processHelloWorldAnycastRequestDispatcher.isReady())
				{
					// Execute the anycast request dispatcher. 02/02/2016, Bing Li
					super.execute(this.processHelloWorldAnycastRequestDispatcher);
				}
				// Enqueue the notification into those anycast request dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.processHelloWorldAnycastRequestDispatcher.enqueue((HelloWorldAnycastRequest)message.getMessage());
				break;

			case MulticastDIPMessageType.HELLO_WORLD_UNICAST_REQUEST:
				System.out.println("<" + ClusterChildSingleton.CLUSTER().getChildIP() + ":" + ClusterChildSingleton.CLUSTER().getChildPort() + ": HELLO_WORLD_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the unicast request dispatcher is ready. 02/02/2016, Bing Li
				if (!this.processHelloWorldUnicastRequestDispatcher.isReady())
				{
					// Execute the unicast request dispatcher. 02/02/2016, Bing Li
					super.execute(this.processHelloWorldUnicastRequestDispatcher);
				}
				// Enqueue the notification into those unicast request dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.processHelloWorldUnicastRequestDispatcher.enqueue((HelloWorldUnicastRequest)message.getMessage());
				break;

			case MulticastDIPMessageType.SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION:
				System.out.println("<" + ClusterChildSingleton.CLUSTER().getChildIP() + ":" + ClusterChildSingleton.CLUSTER().getChildPort() + ": SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Cast the message. 11/27/2014, Bing Li
//				shutdownNotification = (ShutdownChildrenBroadcastNotification)message.getMessage();
				// Check whether the bound broadcast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.processShutdownBroadcastNotificationDispatcher.isReady())
				{
					// Execute the bound broadcast notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.processShutdownBroadcastNotificationDispatcher);
				}
				// Enqueue the notification into those bound broadcast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.processShutdownBroadcastNotificationDispatcher.enqueue((OldShutdownChildrenBroadcastNotification)message.getMessage());

				/*
				// Check whether the bound broadcast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.broadcastShutdownNotificationDispatcher.isReady())
				{
					// Execute the bound notification broadcast dispatcher. 02/02/2016, Bing Li
					super.execute(this.broadcastShutdownNotificationDispatcher);
				}
				// Enqueue the notification into those bound broadcast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.broadcastShutdownNotificationDispatcher.enqueue(shutdownNotification);
				*/
				break;
		}
	}
}
