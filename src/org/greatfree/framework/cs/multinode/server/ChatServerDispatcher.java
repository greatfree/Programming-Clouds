package org.greatfree.framework.cs.multinode.server;

import java.util.Calendar;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.multinode.message.AddPartnerNotification;
import org.greatfree.framework.cs.multinode.message.ChatNotification;
import org.greatfree.framework.cs.multinode.message.ChatPartnerRequest;
import org.greatfree.framework.cs.multinode.message.ChatPartnerResponse;
import org.greatfree.framework.cs.multinode.message.ChatPartnerStream;
import org.greatfree.framework.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.framework.cs.multinode.message.ChatRegistryStream;
import org.greatfree.framework.cs.multinode.message.PollNewChatsRequest;
import org.greatfree.framework.cs.multinode.message.PollNewChatsResponse;
import org.greatfree.framework.cs.multinode.message.PollNewChatsStream;
import org.greatfree.framework.cs.multinode.message.PollNewSessionsRequest;
import org.greatfree.framework.cs.multinode.message.PollNewSessionsResponse;
import org.greatfree.framework.cs.multinode.message.PollNewSessionsStream;
import org.greatfree.framework.cs.twonode.server.ChatRegistryThread;
import org.greatfree.framework.cs.twonode.server.ChatRegistryThreadCreator;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

/*
 * The server dispatcher extends com.greatfree.server.ServerDispatcher to implement a sub server dispatcher. 04/15/2017, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class ChatServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a request dispatcher to respond users' registry requests concurrently. 04/17/2017, Bing Li
	private RequestDispatcher<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread, ChatRegistryThreadCreator> registryRequestDispatcher;
	// Declare a request dispatcher to respond users' chat partner requests concurrently. 04/17/2017, Bing Li
	private RequestDispatcher<ChatPartnerRequest, ChatPartnerStream, ChatPartnerResponse, ChatPartnerRequestThread, ChatPartnerRequestThreadCreator> chatPartnerRequestDispatcher;
	// Declare a notification dispatcher to add friends for the chatting when such a notification is received. 04/18/2016, Bing Li
	private NotificationDispatcher<AddPartnerNotification, AddPartnerThread, AddPartnerThreadCreator> addPartnerNotificationDispatcher;
	// Declare a request dispatcher to respond users' new sessions requests concurrently. 04/17/2017, Bing Li
	private RequestDispatcher<PollNewSessionsRequest, PollNewSessionsStream, PollNewSessionsResponse, PollNewSessionsThread, PollNewSessionsThreadCreator> pollNewSessionsRequestDispatcher;
	// Declare a request dispatcher to respond users' new chat requests concurrently. 04/17/2017, Bing Li
	private RequestDispatcher<PollNewChatsRequest, PollNewChatsStream, PollNewChatsResponse, PollNewChatsThread, PollNewChatsThreadCreator> pollNewChatsRequestDispatcher;
	// Declare a notification dispatcher to add chatting messages to the server when such a notification is received. 04/18/2016, Bing Li
	private NotificationDispatcher<ChatNotification, ChatThread, ChatThreadCreator> chatNotificationDispatcher;

	/*
	 * The constructor of ChatServerDispatcher. 04/15/2017, Bing Li
	 */
//	public ChatServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ChatServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);
		
		this.registryRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread, ChatRegistryThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ChatRegistryThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		this.chatPartnerRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ChatPartnerRequest, ChatPartnerStream, ChatPartnerResponse, ChatPartnerRequestThread, ChatPartnerRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ChatPartnerRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		this.addPartnerNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<AddPartnerNotification, AddPartnerThread, AddPartnerThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new AddPartnerThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		this.pollNewSessionsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PollNewSessionsRequest, PollNewSessionsStream, PollNewSessionsResponse, PollNewSessionsThread, PollNewSessionsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new PollNewSessionsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		this.pollNewChatsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PollNewChatsRequest, PollNewChatsStream, PollNewChatsResponse, PollNewChatsThread, PollNewChatsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new PollNewChatsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		this.chatNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ChatNotification, ChatThread, ChatThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ChatThreadCreator())
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
	 * Shut down the server message dispatcher. 04/15/2017, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.registryRequestDispatcher.dispose();
		this.chatPartnerRequestDispatcher.dispose();
		this.addPartnerNotificationDispatcher.dispose();
		this.pollNewSessionsRequestDispatcher.dispose();
		this.pollNewChatsRequestDispatcher.dispose();
		this.chatNotificationDispatcher.dispose();
	}

	/*
	 * Process the available messages in a concurrent way. 04/17/2017, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
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
				
			case ChatMessageType.CS_CHAT_PARTNER_REQUEST:
				System.out.println("CS_CHAT_PARTNER_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the partner request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.chatPartnerRequestDispatcher.isReady())
				{
					// Execute the partner request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.chatPartnerRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.chatPartnerRequestDispatcher.enqueue(new ChatPartnerStream(message.getOutStream(), message.getLock(), (ChatPartnerRequest)message.getMessage()));
				break;

			case ChatMessageType.CS_ADD_PARTNER_NOTIFICATION:
				System.out.println("CS_ADD_PARTNER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.addPartnerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.addPartnerNotificationDispatcher);
				}
				// Enqueue the instance of AddFriendNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.addPartnerNotificationDispatcher.enqueue((AddPartnerNotification)message.getMessage());
				break;
				
			case ChatMessageType.POLL_NEW_SESSIONS_REQUEST:
				System.out.println("POLL_NEW_SESSIONS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the partner request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.pollNewSessionsRequestDispatcher.isReady())
				{
					// Execute the partner request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.pollNewSessionsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.pollNewSessionsRequestDispatcher.enqueue(new PollNewSessionsStream(message.getOutStream(), message.getLock(), (PollNewSessionsRequest)message.getMessage()));
				break;
				
			case ChatMessageType.POLL_NEW_CHATS_REQUEST:
				System.out.println("POLL_NEW_CHATS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the partner request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.pollNewChatsRequestDispatcher.isReady())
				{
					// Execute the partner request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.pollNewChatsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.pollNewChatsRequestDispatcher.enqueue(new PollNewChatsStream(message.getOutStream(), message.getLock(), (PollNewChatsRequest)message.getMessage()));
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
