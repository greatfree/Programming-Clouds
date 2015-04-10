package com.greatfree.testing.server;

import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.RequestDispatcher;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.InitReadNotification;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.RegisterClientNotification;
import com.greatfree.testing.message.SignUpRequest;
import com.greatfree.testing.message.SignUpResponse;
import com.greatfree.testing.message.SignUpStream;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond clients' requests and receive clients' notifications for the server. 09/20/2014, Bing Li
 */

// Created: 09/20/2014, Bing Li
public class MyServerDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the registration notification concurrently. 11/04/2014, Bing Li
	private NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator> registerClientNotificationDispatcher;
	// Declare a request dispatcher to respond users sign-up requests concurrently. 11/04/2014, Bing Li
	private RequestDispatcher<SignUpRequest, SignUpStream, SignUpResponse, SignUpThread, SignUpThreadCreator> signUpRequestDispatcher;
	// Declare a notification dispatcher to deal with instances of InitReadNotification from a client concurrently such that the client can initialize its ObjectInputStream. 11/09/2014, Bing Li
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;

	/*
	 * Initialize. 09/20/2014, Bing Li
	 */
	public MyServerDispatcher(int corePoolSize, long keepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/04/2014, Bing Li
		super(corePoolSize, keepAliveTime);

		// Initialize the notification dispatcher. 11/30/2014, Bing Li
		this.registerClientNotificationDispatcher = new NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterClientThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		// Set the idle checking. 11/30/2014, Bing Li
		this.registerClientNotificationDispatcher.setIdleChecker(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the notification dispatcher. 11/30/2014, Bing Li
		super.execute(this.registerClientNotificationDispatcher);
		
		// Initialize the sign up dispatcher. 11/04/2014, Bing Li
		this.signUpRequestDispatcher = new RequestDispatcher<SignUpRequest, SignUpStream, SignUpResponse, SignUpThread, SignUpThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new SignUpThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME);
		// Set the parameters to check idle states of threads. 11/04/2014, Bing Li
		this.signUpRequestDispatcher.setIdleChecker(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the dispatcher. 11/04/2014, Bing Li
		super.execute(this.signUpRequestDispatcher);

		// Initialize the notification dispatcher. 11/30/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher = new NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new InitReadFeedbackThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		// Set the idle checking. 11/30/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher.setIdleChecker(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the notification dispatcher. 11/30/2014, Bing Li
		super.execute(this.initReadFeedbackNotificationDispatcher);
	}

	/*
	 * Shut down the server message dispatcher. 09/20/2014, Bing Li
	 */
	public void shutdown()
	{
		// Dispose the sign-up dispatcher. 11/04/2014, Bing Li
		this.signUpRequestDispatcher.dispose();
		// Dispose the dispatcher for initializing reading feedback. 11/09/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher.dispose();
		// Shutdown the derived server dispatcher. 11/04/2014, Bing Li
		super.shutdown();
	}
	
	/*
	 * Process the available messages in a concurrent way. 09/20/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/09/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// If the message is the one of sign-up requests. 11/09/2014, Bing Li
			case MessageType.SIGN_UP_REQUEST:
				// Enqueue the request into the dispatcher for concurrent responding. 11/09/2014, Bing Li
				this.signUpRequestDispatcher.enqueue(new SignUpStream(message.getOutStream(), message.getLock(), (SignUpRequest)message.getMessage()));
				break;

			// If the message is the one of initializing notification. 11/09/2014, Bing Li
			case MessageType.INIT_READ_NOTIFICATION:
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/09/2014, Bing Li
				this.initReadFeedbackNotificationDispatcher.enqueue((InitReadNotification)message.getMessage());
				break;
		}
	}
}
