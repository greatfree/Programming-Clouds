package org.greatfree.dsf.cps.enterprise.db;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.enterprise.message.DeployEntityBeanNotification;
import org.greatfree.dsf.cps.enterprise.message.EnterpriseMessageType;
import org.greatfree.dsf.cps.enterprise.message.EntityBeanRequest;
import org.greatfree.dsf.cps.enterprise.message.EntityBeanResponse;
import org.greatfree.dsf.cps.enterprise.message.EntityBeanStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 04/22/2020, Bing Li
class DBDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<DeployEntityBeanNotification, DeployEntityBeanNotificationThread, DeployEntityBeanNotificationThreadCreator> deployEntityBeanNotificationDispatcher;

	private RequestDispatcher<EntityBeanRequest, EntityBeanStream, EntityBeanResponse, EntityBeanRequestThread, EntityBeanRequestThreadCreator> entityBeanRequestDispatcher;

	public DBDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.deployEntityBeanNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<DeployEntityBeanNotification, DeployEntityBeanNotificationThread, DeployEntityBeanNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new DeployEntityBeanNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.entityBeanRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<EntityBeanRequest, EntityBeanStream, EntityBeanResponse, EntityBeanRequestThread, EntityBeanRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new EntityBeanRequestThreadCreator())
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
		this.deployEntityBeanNotificationDispatcher.dispose();
		this.entityBeanRequestDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case EnterpriseMessageType.DEPLOY_ENTITY_BEAN_NOTIFICATION:
				System.out.println("DEPLOY_ENTITY_BEAN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.deployEntityBeanNotificationDispatcher.isReady())
				{
					super.execute(this.deployEntityBeanNotificationDispatcher);
				}
				this.deployEntityBeanNotificationDispatcher.enqueue((DeployEntityBeanNotification)message.getMessage());
				break;			
				
			case EnterpriseMessageType.ENTITY_BEAN_REQUEST:
				System.out.println("ENTITY_BEAN_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.entityBeanRequestDispatcher.isReady())
				{
					super.execute(this.entityBeanRequestDispatcher);
				}
				this.entityBeanRequestDispatcher.enqueue(new EntityBeanStream(message.getOutStream(), message.getLock(), (EntityBeanRequest)message.getMessage()));
				break;
		}
	}
}
