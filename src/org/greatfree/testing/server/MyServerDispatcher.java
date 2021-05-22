package org.greatfree.testing.server;

import java.util.Calendar;

import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.InitReadNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.server.InitReadFeedbackThread;
import org.greatfree.server.InitReadFeedbackThreadCreator;
import org.greatfree.server.RegisterClientThread;
import org.greatfree.server.RegisterClientThreadCreator;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.RegisterClientNotification;
import org.greatfree.testing.message.ShutdownServerNotification;
import org.greatfree.testing.message.SignUpRequest;
import org.greatfree.testing.message.SignUpResponse;
import org.greatfree.testing.message.SignUpStream;
import org.greatfree.testing.message.TestNotification;
import org.greatfree.testing.message.TestRequest;
import org.greatfree.testing.message.TestResponse;
import org.greatfree.testing.message.TestStream;
import org.greatfree.testing.message.WeatherNotification;
import org.greatfree.testing.message.WeatherRequest;
import org.greatfree.testing.message.WeatherResponse;
import org.greatfree.testing.message.WeatherStream;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond clients' requests and receive clients' notifications for the server. 09/20/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization request dispatcher is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 09/20/2014, Bing Li
//public class MyServerDispatcher extends ServerMessageDispatcher<ServerMessage>
class MyServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the registration notification concurrently. 11/04/2014, Bing Li
	private NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator> registerClientNotificationDispatcher;
	// Declare a request dispatcher to respond users sign-up requests concurrently. 11/04/2014, Bing Li
	private RequestDispatcher<SignUpRequest, SignUpStream, SignUpResponse, SignUpThread, SignUpThreadCreator> signUpRequestDispatcher;
	// Declare a notification dispatcher to set the value of Weather when an instance of WeatherNotification is received. 02/15/2016, Bing Li
	private NotificationDispatcher<WeatherNotification, SetWeatherThread, SetWeatherThreadCreator> setWeatherNotificationDispatcher;
	// Declare a request dispatcher to respond an instance of WeatherResponse to the relevant remote client when an instance of WeatherReques is received. 02/15/2016, Bing Li
	private RequestDispatcher<WeatherRequest, WeatherStream, WeatherResponse, WeatherThread, WeatherThreadCreator> weatherRequestDispatcher;
	// Declare a notification dispatcher to deal with instances of InitReadNotification from a client concurrently such that the client can initialize its ObjectInputStream. 11/09/2014, Bing Li
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;
	// Declare a notification dispatcher to shutdown the server when such a notification is received. 02/15/2016, Bing Li
	private NotificationDispatcher<ShutdownServerNotification, ShutdownThread, ShutdownThreadCreator> shutdownNotificationDispatcher;
	
	private NotificationDispatcher<TestNotification, TestNotificationThread, TestNotificationThreadCreator> testNotificationDispatcher;
	
	private RequestDispatcher<TestRequest, TestStream, TestResponse, TestRequestThread, TestRequestThreadCreator> testRequestDispatcher;

	/*
	 * Initialize. 09/20/2014, Bing Li
	 */
//	public MyServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public MyServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/04/2014, Bing Li
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the client registration notification dispatcher. 11/30/2014, Bing Li
		this.registerClientNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new RegisterClientThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
		
		// Initialize the sign up request dispatcher. 11/04/2014, Bing Li
		this.signUpRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SignUpRequest, SignUpStream, SignUpResponse, SignUpThread, SignUpThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new SignUpThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the weather notification dispatcher. 02/15/2016, Bing Li
		this.setWeatherNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<WeatherNotification, SetWeatherThread, SetWeatherThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new SetWeatherThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the sign up request dispatcher. 11/04/2014, Bing Li
		this.weatherRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<WeatherRequest, WeatherStream, WeatherResponse, WeatherThread, WeatherThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new WeatherThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the read initialization notification dispatcher. 11/30/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new InitReadFeedbackThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the shutdown notification dispatcher. 11/30/2014, Bing Li
		this.shutdownNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownServerNotification, ShutdownThread, ShutdownThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ShutdownThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		this.testNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<TestNotification, TestNotificationThread, TestNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new TestNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		this.testRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<TestRequest, TestStream, TestResponse, TestRequestThread, TestRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new TestRequestThreadCreator())
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
	 * Shut down the server message dispatcher. 09/20/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// Dispose the register dispatcher. 01/14/2016, Bing Li
		this.registerClientNotificationDispatcher.dispose();
		// Dispose the sign-up dispatcher. 11/04/2014, Bing Li
		this.signUpRequestDispatcher.dispose();
		// Dispose the weather notification dispatcher. 02/15/2016, Bing Li
		this.setWeatherNotificationDispatcher.dispose();
		// Dispose the weather request dispatcher. 02/15/2016, Bing Li
		this.weatherRequestDispatcher.dispose();
		// Dispose the dispatcher for initializing reading feedback. 11/09/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher.dispose();
		// Dispose the dispatcher for shutdown. 11/09/2014, Bing Li
		this.shutdownNotificationDispatcher.dispose();

		this.testNotificationDispatcher.dispose();
		this.testRequestDispatcher.dispose();
		
		// Shutdown the derived server dispatcher. 11/04/2014, Bing Li
		super.shutdown(timeout);
	}
	
	/*
	 * Process the available messages in a concurrent way. 09/20/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/09/2014, Bing Li
		switch (message.getMessage().getType())
		{
			case MessageType.REGISTER_CLIENT_NOTIFICATION:
				System.out.println("REGISTER_CLIENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the registry notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.registerClientNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.registerClientNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.registerClientNotificationDispatcher.enqueue((RegisterClientNotification)message.getMessage());
				break;
			
			// If the message is the one of sign-up requests. 11/09/2014, Bing Li
			case MessageType.SIGN_UP_REQUEST:
				System.out.println("SIGN_UP_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the sign-up dispatcher is ready. 01/14/2016, Bing Li
				if (!this.signUpRequestDispatcher.isReady())
				{
					// Execute the sign-up dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.signUpRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 11/09/2014, Bing Li
				this.signUpRequestDispatcher.enqueue(new SignUpStream(message.getOutStream(), message.getLock(), (SignUpRequest)message.getMessage()));
				break;

			// If the message is the one of WeatherNotification. 02/15/2016, Bing Li
			case MessageType.WEATHER_NOTIFICATION:
				System.out.println("WEATHER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the weather notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.setWeatherNotificationDispatcher.isReady())
				{
//					System.out.println("Raise setWeatherNotificationDispatcher");
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.setWeatherNotificationDispatcher);
				}
				// Enqueue the instance of WeatherNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.setWeatherNotificationDispatcher.enqueue((WeatherNotification)message.getMessage());
				break;
				
				// If the message is the one of weather requests. 11/09/2014, Bing Li
			case MessageType.WEATHER_REQUEST:
				System.out.println("WEATHER_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the weather request dispatcher is ready. 02/15/2016, Bing Li
				if (!this.weatherRequestDispatcher.isReady())
				{
//					System.out.println("Raise weatherRequestDispatcher");
					// Execute the weather request dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.weatherRequestDispatcher);
				}
				// Enqueue the instance of WeatherRequest into the dispatcher for concurrent responding. 02/15/2016, Bing Li
				this.weatherRequestDispatcher.enqueue(new WeatherStream(message.getOutStream(), message.getLock(), (WeatherRequest)message.getMessage()));
				break;

			// If the message is the one of initializing notification. 11/09/2014, Bing Li
			case SystemMessageType.INIT_READ_NOTIFICATION:
				System.out.println("INIT_READ_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the reading initialization dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.initReadFeedbackNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.initReadFeedbackNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.initReadFeedbackNotificationDispatcher.enqueue((InitReadNotification)message.getMessage());
				break;
				
			case MessageType.SHUTDOWN_REGULAR_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_REGULAR_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.shutdownNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.shutdownNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.shutdownNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
				
			case MessageType.TEST_NOTIFICATION:
				System.out.println("TEST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the test notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.testNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.testNotificationDispatcher);
				}
				// Enqueue the instance of TestNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.testNotificationDispatcher.enqueue((TestNotification)message.getMessage());
				break;
				
			case MessageType.TEST_REQUEST:
				System.out.println("TEST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the test request dispatcher is ready. 02/15/2016, Bing Li
				if (!this.testRequestDispatcher.isReady())
				{
					// Execute the test request dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.testRequestDispatcher);
				}
				// Enqueue the instance of TestRequest into the dispatcher for concurrent responding. 02/15/2016, Bing Li
				this.testRequestDispatcher.enqueue(new TestStream(message.getOutStream(), message.getLock(), (TestRequest)message.getMessage()));
				break;
		}
	}
}
