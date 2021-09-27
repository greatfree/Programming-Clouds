package org.greatfree.server.container;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.greatfree.util.XPathOnDiskReader;

// Created: 01/09/2019, Bing Li
public class ServerProfile
{
	private final static Logger log = Logger.getLogger("org.greatfree.server.container");

	/*
	 * The server parameters. 01/09/2019, Bing Li
	 */
	private AtomicBoolean isDefault;
	private int port;
	private int listeningThreadCount;
	private int serverThreadPoolSize;
	private long serverThreadKeepAliveTime;
	private int schedulerThreadPoolSize;
	private long schedulerThreadPoolKeepAliveTime;
	
	/*
	 * The notification dispatcher parameters. 01/09/2019, Bing Li
	 */
	private int notificationDispatcherPoolSize;
	private int notificationQueueSize;
	private long notificationDispatcherWaitTime;
	private int notificationDispatcherWaitRound;
	private long notificationDispatcherIdleCheckDelay;
	private long notificationDispatcherIdleCheckPeriod;
	
	/*
	 * The request dispatcher parameters. 01/09/2019, Bing Li
	 */
	private int requestDispatcherPoolSize;
	private int requestQueueSize;
	private long requestDispatcherWaitTime;
	private int requestDispatcherWaitRound;
	private long requestDispatcherIdleCheckDelay;
	private long requestDispatcherIdleCheckPeriod;
	
	private ServerProfile()
	{
		this.isDefault = new AtomicBoolean(true);
	}
	
	private static ServerProfile instance = new ServerProfile();
	
	public static ServerProfile CS()
	{
		if (instance == null)
		{
			instance = new ServerProfile();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void init(String path)
	{
		this.isDefault.set(false);
		XPathOnDiskReader reader = new XPathOnDiskReader(path, true);
//		this.port = new Integer(reader.read(ContainerXML.SELECT_SERVER_PORT));
		this.port = Integer.valueOf(reader.read(ContainerXML.SELECT_SERVER_PORT));
//		this.listeningThreadCount = new Integer(reader.read(ContainerXML.SELECT_LISTENING_THREAD_COUNT));
		this.listeningThreadCount = Integer.valueOf(reader.read(ContainerXML.SELECT_LISTENING_THREAD_COUNT));
//		this.serverThreadPoolSize = new Integer(reader.read(ContainerXML.SELECT_SERVER_THREAD_POOL_SIZE));
		this.serverThreadPoolSize = Integer.valueOf(reader.read(ContainerXML.SELECT_SERVER_THREAD_POOL_SIZE));
//		this.serverThreadKeepAliveTime = new Integer(reader.read(ContainerXML.SELECT_SERVER_THREAD_KEEP_ALIVE_TIME));
		this.serverThreadKeepAliveTime = Integer.valueOf(reader.read(ContainerXML.SELECT_SERVER_THREAD_KEEP_ALIVE_TIME));
//		this.schedulerThreadPoolSize = new Integer(reader.read(ContainerXML.SELECT_SCHEDULER_THREAD_POOL_SIZE));
		this.schedulerThreadPoolSize = Integer.valueOf(reader.read(ContainerXML.SELECT_SCHEDULER_THREAD_POOL_SIZE));
//		this.schedulerThreadPoolKeepAliveTime = new Integer(reader.read(ContainerXML.SELECT_SCHEDULER_THERAD_KEEP_ALIVE_TIME));
		this.schedulerThreadPoolKeepAliveTime = Integer.valueOf(reader.read(ContainerXML.SELECT_SCHEDULER_THERAD_KEEP_ALIVE_TIME));
		
//		this.notificationDispatcherPoolSize = new Integer(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_POOL_SIZE));
		this.notificationDispatcherPoolSize = Integer.valueOf(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_POOL_SIZE));
//		this.notificationQueueSize = new Integer(reader.read(ContainerXML.SELECT_NOTIFICATION_QUEUE_SIZE));
		this.notificationQueueSize = Integer.valueOf(reader.read(ContainerXML.SELECT_NOTIFICATION_QUEUE_SIZE));
//		this.notificationDispatcherWaitTime = new Integer(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_WAIT_TIME));
		this.notificationDispatcherWaitTime = Integer.valueOf(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_WAIT_TIME));
//		this.notificationDispatcherWaitRound = new Integer(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_WAIT_ROUND));
		this.notificationDispatcherWaitRound = Integer.valueOf(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_WAIT_ROUND));
//		this.notificationDispatcherIdleCheckDelay = new Integer(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY));
		this.notificationDispatcherIdleCheckDelay = Integer.valueOf(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY));
//		this.notificationDispatcherIdleCheckPeriod = new Integer(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD));
		this.notificationDispatcherIdleCheckPeriod = Integer.valueOf(reader.read(ContainerXML.SELECT_NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD));
		
//		this.requestDispatcherPoolSize = new Integer(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_POOL_SIZE));
		this.requestDispatcherPoolSize = Integer.valueOf(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_POOL_SIZE));
//		this.requestQueueSize = new Integer(reader.read(ContainerXML.SELECT_REQUEST_QUEUE_SIZE));
		this.requestQueueSize = Integer.valueOf(reader.read(ContainerXML.SELECT_REQUEST_QUEUE_SIZE));
//		this.requestDispatcherWaitTime = new Integer(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_WAIT_TIME));
		this.requestDispatcherWaitTime = Integer.valueOf(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_WAIT_TIME));
//		this.requestDispatcherWaitRound = new Integer(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_WAIT_ROUND));
		this.requestDispatcherWaitRound = Integer.valueOf(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_WAIT_ROUND));
//		this.requestDispatcherIdleCheckDelay = new Integer(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_IDLE_CHECK_DELAY));
		this.requestDispatcherIdleCheckDelay = Integer.valueOf(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_IDLE_CHECK_DELAY));
//		this.requestDispatcherIdleCheckPeriod = new Integer(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_IDLE_CHECK_PERIOD));
		this.requestDispatcherIdleCheckPeriod = Integer.valueOf(reader.read(ContainerXML.SELECT_REQUEST_DISPATCHER_IDLE_CHECK_PERIOD));
		
		reader.close();
	}
	
	public void setDefault(boolean isDefault)
	{
		this.isDefault.set(isDefault);
		log.info("ServerProfile-setDefault(): isDefault = " + this.isDefault);
	}
	
	public boolean isDefault()
	{
		return this.isDefault.get();
	}

	public int getPort()
	{
		return this.port;
	}
	
	public int getListeningThreadCount()
	{
		return this.listeningThreadCount;
	}
	
	public int getServerThreadPoolSize()
	{
		return this.serverThreadPoolSize;
	}
	
	public long getServerThreadKeepAliveTime()
	{
		return this.serverThreadKeepAliveTime;
	}
	
	public int getSchedulerThreadPoolSize()
	{
		return this.schedulerThreadPoolSize;
	}
	
	public long getSchedulerThreadPoolKeepAliveTime()
	{
		return this.schedulerThreadPoolKeepAliveTime;
	}
	
	public int getNotificationDispatcherPoolSize()
	{
		return this.notificationDispatcherPoolSize;
	}
	
	public int getNotificationQueueSize()
	{
		return this.notificationQueueSize;
	}
	
	public long getNotificationDispatcherWaitTime()
	{
		return this.notificationDispatcherWaitTime;
	}
	
	public int getNotificationDispatcherWaitRound()
	{
		return this.notificationDispatcherWaitRound;
	}
	
	public long getNotificationDispatcherIdleCheckDelay()
	{
		return this.notificationDispatcherIdleCheckDelay;
	}
	
	public long getNotificationDispatcherIdleCheckPeriod()
	{
		return this.notificationDispatcherIdleCheckPeriod;
	}
	
	
	public int getRequestDispatcherPoolSize()
	{
		return this.requestDispatcherPoolSize;
	}
	
	public int getRequestQueueSize()
	{
		return this.requestQueueSize;
	}
	
	public long getRequestDispatcherWaitTime()
	{
		return this.requestDispatcherWaitTime;
	}
	
	public int getRequestDispatcherWaitRound()
	{
		return this.requestDispatcherWaitRound;
	}
	
	public long getRequestDispatcherIdleCheckDelay()
	{
		return this.requestDispatcherIdleCheckDelay;
	}
	
	public long getRequestDispatcherIdleCheckPeriod()
	{
		return this.requestDispatcherIdleCheckPeriod;
	}
}
