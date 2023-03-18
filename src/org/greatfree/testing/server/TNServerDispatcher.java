package org.greatfree.testing.server;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.TNNotification;
import org.greatfree.testing.message.TestRequest;
import org.greatfree.testing.message.TestResponse;
import org.greatfree.testing.message.TestStream;

// Created: 04/10/2020, Bing Li
class TNServerDispatcher extends ServerDispatcher<ServerMessage>
{
	private final static Logger log = Logger.getLogger("org.greatfree.testing.server");

	private NotificationDispatcher<TNNotification, TNNotificationThread, TNNotificationThreadCreator> tnNotificationDispatcher;
	private RequestDispatcher<TestRequest, TestStream, TestResponse, TestRequestThread, TestRequestThreadCreator> trRequestDispatcher;

	private NotificationDispatcher<ShutdownServerNotification, ShutdownServerNotificationThread, ShutdownServerNotificationThreadCreator> shutdownNotificationDispatcher;

	public TNServerDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.tnNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<TNNotification, TNNotificationThread, TNNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new TNNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.trRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<TestRequest, TestStream, TestResponse, TestRequestThread, TestRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new TestRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownServerNotification, ShutdownServerNotificationThread, ShutdownServerNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownServerNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.tnNotificationDispatcher.dispose();
		this.trRequestDispatcher.dispose();
		this.shutdownNotificationDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case MessageType.TN_NOTIFICATION:
				log.info("TN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.tnNotificationDispatcher.isReady())
				{
					System.out.println("tnNotificationDispatcher is excuted ...");
					super.execute(this.tnNotificationDispatcher);
				}
				this.tnNotificationDispatcher.enqueue((TNNotification)message.getMessage());
				break;
				
			case MessageType.TEST_REQUEST:
				log.info("TEST_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.trRequestDispatcher.isReady())
				{
					super.execute(this.trRequestDispatcher);
				}
				this.trRequestDispatcher.enqueue(new TestStream(message.getOutStream(), message.getLock(), (TestRequest)message.getMessage()));
				break;

			case SystemMessageType.SHUTDOWN_SERVER_NOTIFICATION:
				log.info("SHUTDOWN_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.shutdownNotificationDispatcher.isReady())
				{
					System.out.println("tnNotificationDispatcher is excuted ...");
					super.execute(this.shutdownNotificationDispatcher);
				}
				this.shutdownNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
		}
	}

}
