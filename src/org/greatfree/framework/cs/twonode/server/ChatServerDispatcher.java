package org.greatfree.framework.cs.twonode.server;

import java.util.Calendar;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.multinode.message.ChatNotification;
import org.greatfree.framework.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.framework.cs.multinode.message.ChatRegistryStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

/*
 * The real code of the SD for ChatServerDispatcher. 05/08/2018, Bing Li
 */

// Created: 05/08/2018, Bing Li
class ChatServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a request dispatcher to respond users' registry requests concurrently. 04/17/2017, Bing Li
	private RequestDispatcher<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread, ChatRegistryThreadCreator> registryRequestDispatcher;
	// Declare a notification dispatcher to add chatting messages to the server when such a notification is received. 04/18/2016, Bing Li
	private NotificationDispatcher<ChatNotification, ChatThread, ChatThreadCreator> chatNotificationDispatcher;

	/*
	 * The constructor of ChatServerDispatcher. 05/08/2018, Bing Li
	 */
//	public ChatServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ChatServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		// Initialize the super constructor, ServerDispatcher. 05/08/2018, Bing Li
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the request dispatcher for the request/response, ChatRegistryRequest/ChatRegistryResponse
		this.registryRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread, ChatRegistryThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.threadPool(super.getThreadPool())
				.threadCreator(new ChatRegistryThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		// Initialize the notification dispatcher for the notification, ChatNotification
		this.chatNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ChatNotification, ChatThread, ChatThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.threadPool(super.getThreadPool())
				.threadCreator(new ChatThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}
	
	/*
	 * Shut down the server message dispatcher. 04/15/2017, Bing Li
	 */
	/*
	public void shutdown(long timeout) throws InterruptedException
	{
		this.registryRequestDispatcher.dispose();
		this.chatNotificationDispatcher.dispose();
		super.shutdown(timeout);
	}
	*/
	
	/*
	 * Process the available messages in a concurrent way. 04/17/2017, Bing Li
	 */
	/*
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 04/17/2017, Bing Li
		switch (message.getMessage().getType())
		{
			case ChatMessageType.CS_CHAT_REGISTRY_REQUEST:
				System.out.println("CS_CHAT_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.registryRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.registryRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.registryRequestDispatcher.enqueue(new ChatRegistryStream(message.getOutStream(), message.getLock(), (ChatRegistryRequest)message.getMessage()));
				break;
				
			case ChatMessageType.CS_CHAT_NOTIFICATION:
				System.out.println("CHAT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.chatNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.chatNotificationDispatcher);
				}
				// Enqueue the instance of ChatNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.chatNotificationDispatcher.enqueue((ChatNotification)message.getMessage());
				break;
		}
		
	}
	*/

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.registryRequestDispatcher.dispose();
		this.chatNotificationDispatcher.dispose();
	}

	/*
	 * The old name is consume(). Now it is updated to process(). 02/17/2020. Bing Li
	 */
	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 04/17/2017, Bing Li
		switch (message.getMessage().getType())
		{
			case ChatMessageType.CS_CHAT_REGISTRY_REQUEST:
				System.out.println("CS_CHAT_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.registryRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.registryRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.registryRequestDispatcher.enqueue(new ChatRegistryStream(message.getOutStream(), message.getLock(), (ChatRegistryRequest)message.getMessage()));
				break;
				
			case ChatMessageType.CS_CHAT_NOTIFICATION:
				System.out.println("CHAT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.chatNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.chatNotificationDispatcher);
				}
				// Enqueue the instance of ChatNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.chatNotificationDispatcher.enqueue((ChatNotification)message.getMessage());
				break;
		}
		
	}
}
