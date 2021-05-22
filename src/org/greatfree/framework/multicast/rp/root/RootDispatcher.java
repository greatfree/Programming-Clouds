package org.greatfree.framework.multicast.rp.root;

import java.util.Calendar;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.ClientHelloWorldAnycastRequest;
import org.greatfree.framework.multicast.message.ClientHelloWorldAnycastResponse;
import org.greatfree.framework.multicast.message.ClientHelloWorldAnycastStream;
import org.greatfree.framework.multicast.message.ClientHelloWorldBroadcastRequest;
import org.greatfree.framework.multicast.message.ClientHelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.message.ClientHelloWorldBroadcastStream;
import org.greatfree.framework.multicast.message.ClientHelloWorldUnicastRequest;
import org.greatfree.framework.multicast.message.ClientHelloWorldUnicastResponse;
import org.greatfree.framework.multicast.message.ClientHelloWorldUnicastStream;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;
import org.greatfree.framework.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastNotification;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;
import org.greatfree.framework.multicast.message.MulticastDIPMessageType;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.message.multicast.RPMulticastResponse;
import org.greatfree.server.ServerDispatcher;

// Created: 10/21/2018, Bing Li
public class RootDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<HelloWorldBroadcastNotification, ClientHelloWorldBroadcastNotificationThread, ClientHelloWorldBroadcastNotificationThreadCreator> clientBroadcastNotificationDispatcher;

	private NotificationDispatcher<HelloWorldAnycastNotification, ClientHelloWorldAnycastNotificationThread, ClientHelloWorldAnycastNotificationThreadCreator> clientAnycastNotificationDispatcher;

	private NotificationDispatcher<HelloWorldUnicastNotification, ClientHelloWorldUnicastNotificationThread, ClientHelloWorldUnicastNotificationThreadCreator> clientUnicastNotificationDispatcher;

	private RequestDispatcher<ClientHelloWorldBroadcastRequest, ClientHelloWorldBroadcastStream, ClientHelloWorldBroadcastResponse, ClientHelloWorldBroadcastRequestThread, ClientHelloWorldBroadcastRequestThreadCreator> clientBroadcastRequestDispatcher;

	private RequestDispatcher<ClientHelloWorldUnicastRequest, ClientHelloWorldUnicastStream, ClientHelloWorldUnicastResponse, ClientHelloWorldUnicastRequestThread, ClientHelloWorldUnicastRequestThreadCreator> clientUnicastRequestDispatcher;

	private RequestDispatcher<ClientHelloWorldAnycastRequest, ClientHelloWorldAnycastStream, ClientHelloWorldAnycastResponse, ClientHelloWorldAnycastRequestThread, ClientHelloWorldAnycastRequestThreadCreator> clientAnycastRequestDispatcher;
	
//	private NotificationDispatcher<HelloWorldBroadcastResponse, HelloWorldBroadcastResponseThread, HelloWorldBroadcastResponseThreadCreator> helloWorldBroadcastResponseDispatcher;

//	private NotificationDispatcher<RPHelloWorldAnycastResponse, HelloWorldAnycastResponseThread, HelloWorldAnycastResponseThreadCreator> helloWorldAnycastResponseDispatcher;
	private NotificationDispatcher<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread, HelloWorldAnycastResponseThreadCreator> helloWorldAnycastResponseDispatcher;

//	private NotificationDispatcher<RPHelloWorldUnicastResponse, HelloWorldUnicastResponseThread, HelloWorldUnicastResponseThreadCreator> helloWorldUnicastResponseDispatcher;
	private NotificationDispatcher<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread, HelloWorldUnicastResponseThreadCreator> helloWorldUnicastResponseDispatcher;

	private NotificationDispatcher<RPMulticastResponse, RootRPMulticastResponseThread, RootRPMulticastResponseThreadCreator> rpResponseDispatcher;

	private NotificationDispatcher<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread, ShutdownChildrenAdminNotificationThreadCreator> shutdownChildrenNotificationDispatcher;

	private NotificationDispatcher<ShutdownServerNotification, ShutdownServerThread, ShutdownServerThreadCreator> shutdownServerNotificationDispatcher;

//	public RootDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public RootDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		this.clientBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldBroadcastNotification, ClientHelloWorldBroadcastNotificationThread, ClientHelloWorldBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ClientHelloWorldBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.clientAnycastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldAnycastNotification, ClientHelloWorldAnycastNotificationThread, ClientHelloWorldAnycastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ClientHelloWorldAnycastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.clientUnicastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldUnicastNotification, ClientHelloWorldUnicastNotificationThread, ClientHelloWorldUnicastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ClientHelloWorldUnicastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		
		this.clientBroadcastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClientHelloWorldBroadcastRequest, ClientHelloWorldBroadcastStream, ClientHelloWorldBroadcastResponse, ClientHelloWorldBroadcastRequestThread, ClientHelloWorldBroadcastRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new ClientHelloWorldBroadcastRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.clientUnicastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClientHelloWorldUnicastRequest, ClientHelloWorldUnicastStream, ClientHelloWorldUnicastResponse, ClientHelloWorldUnicastRequestThread, ClientHelloWorldUnicastRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new ClientHelloWorldUnicastRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.clientAnycastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClientHelloWorldAnycastRequest, ClientHelloWorldAnycastStream, ClientHelloWorldAnycastResponse, ClientHelloWorldAnycastRequestThread, ClientHelloWorldAnycastRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new ClientHelloWorldAnycastRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		// Initialize the notification dispatcher for the notification, HelloWorldBroadcastResponse. 11/27/2014, Bing Li
//		this.helloWorldBroadcastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RPHelloWorldBroadcastResponse, HelloWorldBroadcastResponseThread, HelloWorldBroadcastResponseThreadCreator>()
		/*
		this.helloWorldBroadcastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldBroadcastResponse, HelloWorldBroadcastResponseThread, HelloWorldBroadcastResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloWorldBroadcastResponseThreadCreator())
				.maxTaskSize(ServerConfig.MAX_NOTIFICATION_TASK_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
				*/

		// Initialize the notification dispatcher for the notification, HelloWorldAnycastResponse. 11/27/2014, Bing Li
//		this.helloWorldAnycastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RPHelloWorldAnycastResponse, HelloWorldAnycastResponseThread, HelloWorldAnycastResponseThreadCreator>()
		this.helloWorldAnycastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread, HelloWorldAnycastResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloWorldAnycastResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		// Initialize the notification dispatcher for the notification, HelloWorldUnicastResponse. 11/27/2014, Bing Li
//		this.helloWorldUnicastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RPHelloWorldUnicastResponse, HelloWorldUnicastResponseThread, HelloWorldUnicastResponseThreadCreator>()
		this.helloWorldUnicastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread, HelloWorldUnicastResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloWorldUnicastResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.rpResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RPMulticastResponse, RootRPMulticastResponseThread, RootRPMulticastResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new RootRPMulticastResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		// Initialize the notification dispatcher for the notification, ShutdownChildrenAdminNotification. 11/27/2014, Bing Li
		this.shutdownChildrenNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread, ShutdownChildrenAdminNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownChildrenAdminNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownServerNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownServerNotification, ShutdownServerThread, ShutdownServerThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownServerThreadCreator())
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
		this.clientBroadcastNotificationDispatcher.dispose();
		this.clientAnycastNotificationDispatcher.dispose();
		this.clientUnicastNotificationDispatcher.dispose();
		this.clientBroadcastRequestDispatcher.dispose();
		this.clientUnicastRequestDispatcher.dispose();
		this.clientAnycastRequestDispatcher.dispose();
		
//		this.helloWorldBroadcastResponseDispatcher.dispose();
		this.helloWorldAnycastResponseDispatcher.dispose();
		this.helloWorldUnicastResponseDispatcher.dispose();
		this.rpResponseDispatcher.dispose();
		this.shutdownChildrenNotificationDispatcher.dispose();
		this.shutdownServerNotificationDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 04/17/2017, Bing Li
		switch (message.getMessage().getType())
		{
			case MulticastDIPMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION:
				System.out.println("HELLO_WORLD_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.clientBroadcastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientBroadcastNotificationDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.clientBroadcastNotificationDispatcher.enqueue((HelloWorldBroadcastNotification)message.getMessage());
				break;

			case MulticastDIPMessageType.HELLO_WORLD_ANYCAST_NOTIFICATION:
				System.out.println("HELLO_WORLD_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.clientAnycastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientAnycastNotificationDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.clientAnycastNotificationDispatcher.enqueue((HelloWorldAnycastNotification)message.getMessage());
				break;

			case MulticastDIPMessageType.HELLO_WORLD_UNICAST_NOTIFICATION:
				System.out.println("HELLO_WORLD_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.clientUnicastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientUnicastNotificationDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.clientUnicastNotificationDispatcher.enqueue((HelloWorldUnicastNotification)message.getMessage());
				break;

			case MulticastDIPMessageType.CLIENT_HELLO_WORLD_BROADCAST_REQUEST:
				System.out.println("CLIENT_HELLO_WORLD_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.clientBroadcastRequestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientBroadcastRequestDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.clientBroadcastRequestDispatcher.enqueue(new ClientHelloWorldBroadcastStream(message.getOutStream(), message.getLock(), (ClientHelloWorldBroadcastRequest)message.getMessage()));
				break;

			case MulticastDIPMessageType.CLIENT_HELLO_WORLD_UNICAST_REQUEST:
				System.out.println("CLIENT_HELLO_WORLD_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.clientUnicastRequestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientUnicastRequestDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.clientUnicastRequestDispatcher.enqueue(new ClientHelloWorldUnicastStream(message.getOutStream(), message.getLock(), (ClientHelloWorldUnicastRequest)message.getMessage()));
				break;

			case MulticastDIPMessageType.CLIENT_HELLO_WORLD_ANYCAST_REQUEST:
				System.out.println("CLIENT_HELLO_WORLD_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.clientAnycastRequestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientAnycastRequestDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.clientAnycastRequestDispatcher.enqueue(new ClientHelloWorldAnycastStream(message.getOutStream(), message.getLock(), (ClientHelloWorldAnycastRequest)message.getMessage()));
				break;
		
				/*
			case MulticastMessageType.HELLO_WORLD_BROADCAST_RESPONSE:
				System.out.println("HELLO_WORLD_BROADCAST_RESPONSE received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.helloWorldBroadcastResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.helloWorldBroadcastResponseDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
//				this.helloWorldBroadcastResponseDispatcher.enqueue((RPHelloWorldBroadcastResponse)message.getMessage());
				this.helloWorldBroadcastResponseDispatcher.enqueue((HelloWorldBroadcastResponse)message.getMessage());
				break;
				*/
				
			case MulticastDIPMessageType.HELLO_WORLD_ANYCAST_RESPONSE:
				System.out.println("HELLO_WORLD_ANYCAST_RESPONSE received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.helloWorldAnycastResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.helloWorldAnycastResponseDispatcher);
				}
				// Enqueue the instance of HelloWorldAnycastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
//				this.helloWorldAnycastResponseDispatcher.enqueue((RPHelloWorldAnycastResponse)message.getMessage());
				this.helloWorldAnycastResponseDispatcher.enqueue((HelloWorldAnycastResponse)message.getMessage());
				break;

			case MulticastDIPMessageType.HELLO_WORLD_UNICAST_RESPONSE:
				System.out.println("HELLO_WORLD_UNICAST_RESPONSE received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.helloWorldUnicastResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.helloWorldUnicastResponseDispatcher);
				}
				// Enqueue the instance of HelloWorldUnicastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
//				this.helloWorldUnicastResponseDispatcher.enqueue((RPHelloWorldUnicastResponse)message.getMessage());
				this.helloWorldUnicastResponseDispatcher.enqueue((HelloWorldUnicastResponse)message.getMessage());
				break;
				
			case SystemMessageType.RP_MULTICAST_RESPONSE:
				System.out.println("RP_MULTICAST_RESPONSE received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.rpResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.rpResponseDispatcher);
				}
				// Enqueue the instance of HelloWorldUnicastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.rpResponseDispatcher.enqueue((RPMulticastResponse)message.getMessage());
				break;

			case MulticastDIPMessageType.SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION:
				System.out.println("SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.shutdownChildrenNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.shutdownChildrenNotificationDispatcher);
				}
				// Enqueue the instance of ShutdownChildrenAdminNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.shutdownChildrenNotificationDispatcher.enqueue((ShutdownChildrenAdminNotification)message.getMessage());
				break;
				
			case ChatMessageType.SHUTDOWN_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.shutdownServerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.shutdownServerNotificationDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.shutdownServerNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
		}
	}

}
