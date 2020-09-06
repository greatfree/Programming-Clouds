package org.greatfree.dsf.cps.threetier.terminal;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.threetier.message.CPSMessageType;
import org.greatfree.dsf.cps.threetier.message.CoordinatorNotification;
import org.greatfree.dsf.cps.threetier.message.CoordinatorRequest;
import org.greatfree.dsf.cps.threetier.message.CoordinatorResponse;
import org.greatfree.dsf.cps.threetier.message.CoordinatorStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 07/06/2018, Bing Li
class TerminalDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<CoordinatorNotification, CoordinatorNotificationThread, CoordinatorNotificationThreadCreator> coordinatorNotificationDispatcher;
	private RequestDispatcher<CoordinatorRequest, CoordinatorStream, CoordinatorResponse, CoordinatorRequestThread, CoordinatorRequestThreadCreator> coordinatorRequestDispatcher;

//	public TerminalDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public TerminalDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, CoordinatorNotification. 07/07/2018, Bing Li
		this.coordinatorNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<CoordinatorNotification, CoordinatorNotificationThread, CoordinatorNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new CoordinatorNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		// Initialize the request dispatcher for the request/response, CoordinatorRequest/CoordinatorResponse
		this.coordinatorRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CoordinatorRequest, CoordinatorStream, CoordinatorResponse, CoordinatorRequestThread, CoordinatorRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new CoordinatorRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.coordinatorNotificationDispatcher.dispose();
		this.coordinatorRequestDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case CPSMessageType.COORDINATOR_NOTIFICATION:
				System.out.println("COORDINATOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.coordinatorNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.coordinatorNotificationDispatcher);
				}
				// Enqueue the instance of CoordinatorNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.coordinatorNotificationDispatcher.enqueue((CoordinatorNotification)message.getMessage());
				break;
				
			case CPSMessageType.COORDINATOR_REQUEST:
				System.out.println("COORDINATOR_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.coordinatorRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.coordinatorRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.coordinatorRequestDispatcher.enqueue(new CoordinatorStream(message.getOutStream(), message.getLock(), (CoordinatorRequest)message.getMessage()));
				break;
		}
		
		
	}

}
