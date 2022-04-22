package org.greatfree.app.business.cs.multinode.server;

import java.util.Calendar;

import org.greatfree.chat.message.cs.business.BusinessMessageType;
import org.greatfree.chat.message.cs.business.CheckCartRequest;
import org.greatfree.chat.message.cs.business.CheckCartResponse;
import org.greatfree.chat.message.cs.business.CheckCartStream;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionRequest;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionResponse;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionStream;
import org.greatfree.chat.message.cs.business.CheckMerchandiseRequest;
import org.greatfree.chat.message.cs.business.CheckMerchandiseResponse;
import org.greatfree.chat.message.cs.business.CheckMerchandiseStream;
import org.greatfree.chat.message.cs.business.CheckPendingOrderRequest;
import org.greatfree.chat.message.cs.business.CheckPendingOrderResponse;
import org.greatfree.chat.message.cs.business.CheckPendingOrderStream;
import org.greatfree.chat.message.cs.business.CheckSalesRequest;
import org.greatfree.chat.message.cs.business.CheckSalesResponse;
import org.greatfree.chat.message.cs.business.CheckSalesStream;
import org.greatfree.chat.message.cs.business.CheckVendorTransactionRequest;
import org.greatfree.chat.message.cs.business.CheckVendorTransactionResponse;
import org.greatfree.chat.message.cs.business.CheckVendorTransactionStream;
import org.greatfree.chat.message.cs.business.PlaceOrderNotification;
import org.greatfree.chat.message.cs.business.PostMerchandiseNotification;
import org.greatfree.chat.message.cs.business.PutIntoCartNotification;
import org.greatfree.chat.message.cs.business.RemoveFromCartNotification;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.framework.cs.multinode.message.ChatRegistryStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;

/*
 * The server dispatcher extends com.greatfree.server.ServerDispatcher to implement a sub server dispatcher. 04/15/2017, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class ChatServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a request dispatcher to respond users' registry requests concurrently. 04/17/2017, Bing Li
	private RequestDispatcher<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread, ChatRegistryThreadCreator> registryRequestDispatcher;
	
	/*
	 * The following lines implement the platform programming for an e-business system. 12/05/2017, Bing Li
	 */
	// Declare a notification dispatcher to process the merchandise-posting notifications. 12/05/2017, Bing Li
	private NotificationDispatcher<PostMerchandiseNotification, PostMerchandiseThread, PostMerchandiseThreadCreator> postMerchandiseNotificationDispatcher;
	
	// Declare a request dispatcher to process the merchandise-checking requests. 12/05/2017, Bing Li
	private RequestDispatcher<CheckMerchandiseRequest, CheckMerchandiseStream, CheckMerchandiseResponse, CheckMerchandiseThread, CheckMerchandiseThreadCreator> checkMerchandiseRequestDispatcher;

	// Declare a request dispatcher to process the sales-checking requests. 12/05/2017, Bing Li
	private RequestDispatcher<CheckSalesRequest, CheckSalesStream, CheckSalesResponse, CheckSalesThread, CheckSalesThreadCreator> checkSalesRequestDispatcher;

	// Declare a notification dispatcher to process the putting-into-cart notifications. 12/05/2017, Bing Li
	private NotificationDispatcher<PutIntoCartNotification, PutIntoCartThread, PutIntoCartThreadCreator> putIntoCartNotificationDispatcher;
	
	// Declare a request dispatcher to process the cart-checking requests. 12/05/2017, Bing Li
	private RequestDispatcher<CheckCartRequest, CheckCartStream, CheckCartResponse, CheckCartThread, CheckCartThreadCreator> checkCartRequestDispatcher;

	// Declare a request dispatcher to process the pending-orders-checking requests. 12/05/2017, Bing Li
	private RequestDispatcher<CheckPendingOrderRequest, CheckPendingOrderStream, CheckPendingOrderResponse, CheckPendingOrderThread, CheckPendingOrderThreadCreator> checkPendingOrderRequestDispatcher;

	// Declare a notification dispatcher to process the removing-from-cart notifications. 12/05/2017, Bing Li
	private NotificationDispatcher<RemoveFromCartNotification, RemoveFromCartThread, RemoveFromCartThreadCreator> removeFromCartNotificationDispatcher;

	// Declare a notification dispatcher to process the placing-order requests. 12/05/2017, Bing Li
	private NotificationDispatcher<PlaceOrderNotification, PlaceOrderThread, PlaceOrderThreadCreator> placeOrderNotificationDispatcher;

	// Declare a request dispatcher to process the vendor-transaction-checking requests. 12/05/2017, Bing Li
	private RequestDispatcher<CheckVendorTransactionRequest, CheckVendorTransactionStream, CheckVendorTransactionResponse, CheckVendorTransactionThread, CheckVendorTransactionThreadCreator> checkVendorTransactionRequestDispatcher;

	// Declare a request dispatcher to process the customer-transaction-checking requests. 12/05/2017, Bing Li
	private RequestDispatcher<CheckCustomerTransactionRequest, CheckCustomerTransactionStream, CheckCustomerTransactionResponse, CheckCustomerTransactionThread, CheckCustomerTransactionThreadCreator> checkCustomerTransactionRequestDispatcher;

	/*
	 * The constructor of ChatServerDispatcher. 04/15/2017, Bing Li
	 */
//	public ChatServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ChatServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the chatting-registry-request dispatcher. 12/05/2017, Bing Li
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

		// Initialize the merchandise-posting-notification dispatcher. 12/05/2017, Bing Li
		this.postMerchandiseNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PostMerchandiseNotification, PostMerchandiseThread, PostMerchandiseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new PostMerchandiseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the merchandise-checking-request dispatcher. 12/05/2017, Bing Li
		this.checkMerchandiseRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CheckMerchandiseRequest, CheckMerchandiseStream, CheckMerchandiseResponse, CheckMerchandiseThread, CheckMerchandiseThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new CheckMerchandiseThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		// Initialize the sales-checking-request dispatcher. 12/05/2017, Bing Li
		this.checkSalesRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CheckSalesRequest, CheckSalesStream, CheckSalesResponse, CheckSalesThread, CheckSalesThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new CheckSalesThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the put-into-cart-notification dispatcher. 12/05/2017, Bing Li
		this.putIntoCartNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PutIntoCartNotification, PutIntoCartThread, PutIntoCartThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new PutIntoCartThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the cart-checking-request dispatcher. 12/05/2017, Bing Li
		this.checkCartRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CheckCartRequest, CheckCartStream, CheckCartResponse, CheckCartThread, CheckCartThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new CheckCartThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the pending-orders-checking-request dispatcher. 12/05/2017, Bing Li
		this.checkPendingOrderRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CheckPendingOrderRequest, CheckPendingOrderStream, CheckPendingOrderResponse, CheckPendingOrderThread, CheckPendingOrderThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new CheckPendingOrderThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the put-into-cart-notification dispatcher. 12/05/2017, Bing Li
		this.removeFromCartNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RemoveFromCartNotification, RemoveFromCartThread, RemoveFromCartThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new RemoveFromCartThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the put-into-cart-notification dispatcher. 12/05/2017, Bing Li
		this.placeOrderNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PlaceOrderNotification, PlaceOrderThread, PlaceOrderThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new PlaceOrderThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the vendor-transaction-checking-request dispatcher. 12/05/2017, Bing Li
		this.checkVendorTransactionRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CheckVendorTransactionRequest, CheckVendorTransactionStream, CheckVendorTransactionResponse, CheckVendorTransactionThread, CheckVendorTransactionThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new CheckVendorTransactionThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the customer-transaction-checking-request dispatcher. 12/05/2017, Bing Li
		this.checkCustomerTransactionRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CheckCustomerTransactionRequest, CheckCustomerTransactionStream, CheckCustomerTransactionResponse, CheckCustomerTransactionThread, CheckCustomerTransactionThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new CheckCustomerTransactionThreadCreator())
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
	 * Shut down the server message dispatcher. 04/15/2017, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.registryRequestDispatcher.dispose();
		this.postMerchandiseNotificationDispatcher.dispose();
		this.checkMerchandiseRequestDispatcher.dispose();
		this.checkSalesRequestDispatcher.dispose();
		this.putIntoCartNotificationDispatcher.dispose();
		this.checkCartRequestDispatcher.dispose();
		this.checkPendingOrderRequestDispatcher.dispose();
		this.removeFromCartNotificationDispatcher.dispose();
		this.placeOrderNotificationDispatcher.dispose();
		this.checkVendorTransactionRequestDispatcher.dispose();
		this.checkCustomerTransactionRequestDispatcher.dispose();
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
			case SystemMessageType.CS_CHAT_REGISTRY_REQUEST:
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

			case BusinessMessageType.POST_MERCHANDISE_NOTIFICATION:
				System.out.println("POST_MERCHANDISE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the posting-merchandise notification dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postMerchandiseNotificationDispatcher.isReady())
				{
					// Execute the posting-merchandise notification dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postMerchandiseNotificationDispatcher);
				}
				// Enqueue the instance of PostMerchandiseNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.postMerchandiseNotificationDispatcher.enqueue((PostMerchandiseNotification)message.getMessage());
				break;
				
			case BusinessMessageType.CHECK_MERCHANDISE_REQUEST:
				System.out.println("CHECK_MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the checking-merchandise request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.checkMerchandiseRequestDispatcher.isReady())
				{
					// Execute the checking-merchandise request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.checkMerchandiseRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.checkMerchandiseRequestDispatcher.enqueue(new CheckMerchandiseStream(message.getOutStream(), message.getLock(), (CheckMerchandiseRequest)message.getMessage()));
				break;

			case BusinessMessageType.CHECK_SALES_REQUEST:
				System.out.println("CHECK_SALES_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the checking-sales request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.checkSalesRequestDispatcher.isReady())
				{
					// Execute the checking-sales request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.checkSalesRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.checkSalesRequestDispatcher.enqueue(new CheckSalesStream(message.getOutStream(), message.getLock(), (CheckSalesRequest)message.getMessage()));
				break;

			case BusinessMessageType.PUT_INTO_CART_NOTIFICATION:
				System.out.println("PUT_INTO_CART_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the putting-into-cart notification dispatcher is ready. 04/17/2017, Bing Li
				if (!this.putIntoCartNotificationDispatcher.isReady())
				{
					// Execute the putting-into-cart notification dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.putIntoCartNotificationDispatcher);
				}
				// Enqueue the instance of PutIntoCartNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.putIntoCartNotificationDispatcher.enqueue((PutIntoCartNotification)message.getMessage());
				break;
				
			case BusinessMessageType.CHECK_CART_REQUEST:
				System.out.println("CHECK_CART_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the checking-cart request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.checkCartRequestDispatcher.isReady())
				{
					// Execute the checking-cart request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.checkCartRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.checkCartRequestDispatcher.enqueue(new CheckCartStream(message.getOutStream(), message.getLock(), (CheckCartRequest)message.getMessage()));
				break;
				
			case BusinessMessageType.CHECK_PENDING_ORDER_REQUEST:
				System.out.println("CHECK_PENDING_ORDER_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the checking-pending-orders request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.checkPendingOrderRequestDispatcher.isReady())
				{
					// Execute the checking-pending-orders request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.checkPendingOrderRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.checkPendingOrderRequestDispatcher.enqueue(new CheckPendingOrderStream(message.getOutStream(), message.getLock(), (CheckPendingOrderRequest)message.getMessage()));
				break;
				
			case BusinessMessageType.REMOVE_FROM_CART_NOTIFICATION:
				System.out.println("REMOVE_FROM_CART_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the removing-from-cart notification dispatcher is ready. 04/17/2017, Bing Li
				if (!this.removeFromCartNotificationDispatcher.isReady())
				{
					// Execute the removing-from-cart notification dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.removeFromCartNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.removeFromCartNotificationDispatcher.enqueue((RemoveFromCartNotification)message.getMessage());
				break;
				
			case BusinessMessageType.PLACE_ORDER_NOTIFICATION:
				System.out.println("PLACE_ORDER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the placing-order notification dispatcher is ready. 04/17/2017, Bing Li
				if (!this.placeOrderNotificationDispatcher.isReady())
				{
					// Execute the placing-order notification dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.placeOrderNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.placeOrderNotificationDispatcher.enqueue((PlaceOrderNotification)message.getMessage());
				break;
				
			case BusinessMessageType.CHECK_VENDOR_TRANSACTION_REQUEST:
				System.out.println("CHECK_VENDOR_TRANSACTION_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the checking-vendor-transaction request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.checkVendorTransactionRequestDispatcher.isReady())
				{
					// Execute the checking-vendor-transaction request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.checkVendorTransactionRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.checkVendorTransactionRequestDispatcher.enqueue(new CheckVendorTransactionStream(message.getOutStream(), message.getLock(), (CheckVendorTransactionRequest)message.getMessage()));
				break;
				
			case BusinessMessageType.CHECK_CUSTOMER_TRANSACTION_REQUEST:
				System.out.println("CHECK_CUSTOMER_TRANSACTION_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the checking-customer-transaction request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.checkCustomerTransactionRequestDispatcher.isReady())
				{
					// Execute the checking-customer-transaction request dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.checkCustomerTransactionRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.checkCustomerTransactionRequestDispatcher.enqueue(new CheckCustomerTransactionStream(message.getOutStream(), message.getLock(), (CheckCustomerTransactionRequest)message.getMessage()));
				break;
		}
	}
}
