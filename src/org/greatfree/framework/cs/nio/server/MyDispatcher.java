package org.greatfree.framework.cs.nio.server;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.nio.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.nio.message.MyNotification;
import org.greatfree.framework.cs.nio.message.MyRequest;
import org.greatfree.framework.cs.nio.message.MyResponse;
import org.greatfree.framework.cs.nio.message.MyStream;
import org.greatfree.framework.cs.nio.message.NIOMsgType;
import org.greatfree.framework.cs.nio.message.StopServerNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.nio.MessageStream;
import org.greatfree.server.nio.ServerDispatcher;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
class MyDispatcher extends ServerDispatcher<ServerMessage>
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cs.nio.server");
	
	private RequestDispatcher<MyRequest, MyStream, MyResponse, MyRequestThread, MyRequestThreadCreator> myRequestDispatcher;
	private NotificationDispatcher<MyNotification, MyNotificationThread, MyNotificationThreadCreator> myNotificationDispatcher;
	private NotificationDispatcher<StopServerNotification, StopServerNotificationThread, StopServerNotificationThreadCreator> stopDispatcher;

	public MyDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
		
		this.myRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<MyRequest, MyStream, MyResponse, MyRequestThread, MyRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new MyRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.myNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<MyNotification, MyNotificationThread, MyNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new MyNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.stopDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<StopServerNotification, StopServerNotificationThread, StopServerNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new StopServerNotificationThreadCreator())
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
		this.myRequestDispatcher.dispose();
		this.myNotificationDispatcher.dispose();
		this.stopDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case NIOMsgType.MY_REQUEST:
				log.info("MY_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.myRequestDispatcher.isReady())
				{
					super.execute(this.myRequestDispatcher);
				}
				this.myRequestDispatcher.enqueue(new MyStream(message.getChannel(), (MyRequest)message.getMessage()));
				break;

			case NIOMsgType.MY_NOTIFICATION:
				log.info("MY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.myNotificationDispatcher.isReady())
				{
					super.execute(this.myNotificationDispatcher);
				}
				this.myNotificationDispatcher.enqueue((MyNotification)message.getMessage());
				break;
				
			case NIOMsgType.STOP_SERVER_NOTIFICATION:
				log.info("STOP_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.stopDispatcher.isReady())
				{
					super.execute(this.stopDispatcher);
				}
				this.stopDispatcher.enqueue((StopServerNotification)message.getMessage());
				break;
		}
		
	}

}
