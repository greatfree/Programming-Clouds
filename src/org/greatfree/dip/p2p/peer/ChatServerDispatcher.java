package org.greatfree.dip.p2p.peer;

import java.util.Calendar;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.message.AddPartnerNotification;
import org.greatfree.dip.p2p.message.ChatNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

/*
 * The server dispatcher is installed on the Peer rather than a CSServer. Therefore, the messages to be handled become different. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
public class ChatServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to add partner invitation to the peer when such a notification is received. 04/18/2016, Bing Li
	private NotificationDispatcher<AddPartnerNotification, AddPartnerThread, AddPartnerThreadCreator> partnerNotificationDispatcher;
	// Declare a notification dispatcher to display chats to the peer when such a notification is received. 04/18/2016, Bing Li
	private NotificationDispatcher<ChatNotification, ChatThread, ChatThreadCreator> chatNotificationDispatcher;

//	public ChatServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ChatServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		this.partnerNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<AddPartnerNotification, AddPartnerThread, AddPartnerThreadCreator>()
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
	/*
	public void shutdown(long timeout) throws InterruptedException
	{
		this.partnerNotificationDispatcher.dispose();
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
			case ChatMessageType.PEER_ADD_PARTNER_NOTIFICATION:
				System.out.println("PEER_ADD_PARTNER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding chatting partner notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.partnerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.partnerNotificationDispatcher);
				}
				// Enqueue the instance of AddPartnerNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.partnerNotificationDispatcher.enqueue((AddPartnerNotification)message.getMessage());
				break;
				
			case ChatMessageType.PEER_CHAT_NOTIFICATION:
				System.out.println("PEER_CHAT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the chatting notification dispatcher is ready or not. 02/15/2016, Bing Li
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
		this.partnerNotificationDispatcher.dispose();
		this.chatNotificationDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 04/17/2017, Bing Li
		switch (message.getMessage().getType())
		{	
			case ChatMessageType.PEER_ADD_PARTNER_NOTIFICATION:
				System.out.println("PEER_ADD_PARTNER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding chatting partner notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.partnerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.partnerNotificationDispatcher);
				}
				// Enqueue the instance of AddPartnerNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.partnerNotificationDispatcher.enqueue((AddPartnerNotification)message.getMessage());
				break;
				
			case ChatMessageType.PEER_CHAT_NOTIFICATION:
				System.out.println("PEER_CHAT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the chatting notification dispatcher is ready or not. 02/15/2016, Bing Li
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
