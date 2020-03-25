package org.greatfree.dip.p2p.registry;

import java.util.Calendar;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.message.ChatPartnerRequest;
import org.greatfree.dip.p2p.message.ChatPartnerResponse;
import org.greatfree.dip.p2p.message.ChatPartnerStream;
import org.greatfree.dip.p2p.message.ChatRegistryRequest;
import org.greatfree.dip.p2p.message.ChatRegistryResponse;
import org.greatfree.dip.p2p.message.ChatRegistryStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

/*
 * The server dispatcher is responsible for receiving chatting registry messages and responding chatting requests. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
class ChatRegistryDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a request dispatcher to respond users' registry requests concurrently. 04/17/2017, Bing Li
	private RequestDispatcher<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread, ChatRegistryThreadCreator> registryRequestDispatcher;
	// Declare a request dispatcher to respond users' chat partner requests concurrently. 04/17/2017, Bing Li
	private RequestDispatcher<ChatPartnerRequest, ChatPartnerStream, ChatPartnerResponse, ChatPartnerRequestThread, ChatPartnerRequestThreadCreator> chatPartnerRequestDispatcher;

	/*
	 * The constructor of RegistryDispatcher. 04/30/2017, Bing Li
	 */
//	public ChatRegistryDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ChatRegistryDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
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
	}

	/*
	 * Shut down the server message dispatcher. 04/30/2017, Bing Li
	 */
	/*
	public void shutdown(long timeout) throws InterruptedException
	{
		this.registryRequestDispatcher.dispose();
		this.chatPartnerRequestDispatcher.dispose();
		super.shutdown(timeout);
	}
	*/
	
	/*
	 * Process the available messages in a concurrent way. 04/30/2017, Bing Li
	 */
	/*
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 04/30/2017, Bing Li
		switch (message.getMessage().getType())
		{
			case ChatMessageType.PEER_CHAT_REGISTRY_REQUEST:
				System.out.println("PEER_CHAT_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.registryRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.registryRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.registryRequestDispatcher.enqueue(new ChatRegistryStream(message.getOutStream(), message.getLock(), (ChatRegistryRequest)message.getMessage()));
				break;
				
			case ChatMessageType.PEER_CHAT_PARTNER_REQUEST:
				System.out.println("PEER_CHAT_PARTNER_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the partner request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.chatPartnerRequestDispatcher.isReady())
				{
					// Execute the partner request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.chatPartnerRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.chatPartnerRequestDispatcher.enqueue(new ChatPartnerStream(message.getOutStream(), message.getLock(), (ChatPartnerRequest)message.getMessage()));
				break;
		}
	}
	*/

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.registryRequestDispatcher.dispose();
		this.chatPartnerRequestDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 04/30/2017, Bing Li
		switch (message.getMessage().getType())
		{
			case ChatMessageType.PEER_CHAT_REGISTRY_REQUEST:
				System.out.println("PEER_CHAT_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.registryRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.registryRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.registryRequestDispatcher.enqueue(new ChatRegistryStream(message.getOutStream(), message.getLock(), (ChatRegistryRequest)message.getMessage()));
				break;
				
			case ChatMessageType.PEER_CHAT_PARTNER_REQUEST:
				System.out.println("PEER_CHAT_PARTNER_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the partner request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.chatPartnerRequestDispatcher.isReady())
				{
					// Execute the partner request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.chatPartnerRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.chatPartnerRequestDispatcher.enqueue(new ChatPartnerStream(message.getOutStream(), message.getLock(), (ChatPartnerRequest)message.getMessage()));
				break;
		}
	}
	
}
