package org.greatfree.server.container;

import org.greatfree.util.XPathOnDiskReader;

// Created: 01/11/2019, Bing Li
public class PeerProfile
{
	private String peerName;
	private String registryServerIP;
	private int registryServerPort;
	private boolean isRegistryNeeded;
	private int freeClientPoolSize;
	private int readerClientSize;
	private long syncEventerIdleCheckDelay;
	private long syncEventerIdleCheckPeriod;
	private long syncEventerMaxIdleTime;
	private int asyncEventQueueSize;
	private int asyncEventerSize;
	private long asyncEventingWaitTime;
	private long asyncEventQueueWaitTime;
//	private int asyncEventerWaitRound;
	private long asyncEventIdleCheckDelay;
	private long asyncEventIdleCheckPeriod;
	private int schedulerPoolSize;
	private long schedulerKeepAliveTime;
	
	private PeerProfile()
	{
//		ServerProfile.CS().setDefault(true);
	}
	
	private static PeerProfile instance = new PeerProfile();
	
	public static PeerProfile P2P()
	{
		if (instance == null)
		{
			instance = new PeerProfile();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void init(String path)
	{
//		ServerProfile.CS().init(path);
		XPathOnDiskReader reader = new XPathOnDiskReader(path, true);
		
		this.peerName = reader.read(ContainerXML.SELECT_PEER_NAME);
		this.registryServerIP = reader.read(ContainerXML.SELECT_REGISTRY_SERVER_IP);
//		this.registryServerPort = new Integer(reader.read(ContainerXML.SELECT_REGISTRY_SERVER_PORT));
		this.registryServerPort = Integer.valueOf(reader.read(ContainerXML.SELECT_REGISTRY_SERVER_PORT));
//		this.isRegistryNeeded = new Boolean(reader.read(ContainerXML.SELECT_IS_REGISTRY_NEEDED));
		this.isRegistryNeeded = Boolean.valueOf(reader.read(ContainerXML.SELECT_IS_REGISTRY_NEEDED));
//		this.freeClientPoolSize = new Integer(reader.read(ContainerXML.SELECT_FREE_CLIENT_POOL_SIZE));
		this.freeClientPoolSize = Integer.valueOf(reader.read(ContainerXML.SELECT_FREE_CLIENT_POOL_SIZE));
//		this.readerClientSize = new Integer(reader.read(ContainerXML.SELECT_READER_CLIENT_SIZE));
		this.readerClientSize = Integer.valueOf(reader.read(ContainerXML.SELECT_READER_CLIENT_SIZE));
//		this.syncEventerIdleCheckDelay = new Integer(reader.read(ContainerXML.SELECT_SYNC_EVENTER_IDLE_CHECK_DELAY));
		this.syncEventerIdleCheckDelay = Integer.valueOf(reader.read(ContainerXML.SELECT_SYNC_EVENTER_IDLE_CHECK_DELAY));
//		this.syncEventerIdleCheckPeriod = new Integer(reader.read(ContainerXML.SELECT_SYNC_EVENTER_IDLE_CHECK_PERIOD));
		this.syncEventerIdleCheckPeriod = Integer.valueOf(reader.read(ContainerXML.SELECT_SYNC_EVENTER_IDLE_CHECK_PERIOD));
//		this.syncEventerMaxIdleTime = new Integer(reader.read(ContainerXML.SELECT_SYNC_EVENTER_MAX_IDLE_TIME));
		this.syncEventerMaxIdleTime = Integer.valueOf(reader.read(ContainerXML.SELECT_SYNC_EVENTER_MAX_IDLE_TIME));
//		this.asyncEventQueueSize = new Integer(reader.read(ContainerXML.SELECT_ASYNC_EVENT_QUEUE_SIZE));
		this.asyncEventQueueSize = Integer.valueOf(reader.read(ContainerXML.SELECT_ASYNC_EVENT_QUEUE_SIZE));
//		this.asyncEventerSize = new Integer(reader.read(ContainerXML.SELECT_ASYNC_EVENTER_SIZE));
		this.asyncEventerSize = Integer.valueOf(reader.read(ContainerXML.SELECT_ASYNC_EVENTER_SIZE));
//		this.asyncEventingWaitTime = new Integer(reader.read(ContainerXML.SELECT_ASYNC_EVENTING_WAIT_TIME));
		this.asyncEventingWaitTime = Integer.valueOf(reader.read(ContainerXML.SELECT_ASYNC_EVENTING_WAIT_TIME));
//		this.asyncEventerWaitTime = new Integer(reader.read(ContainerXML.SELECT_ASYNC_EVENTER_WAIT_TIME));
		this.asyncEventQueueWaitTime = Integer.valueOf(reader.read(ContainerXML.SELECT_ASYNC_EVENTER_WAIT_TIME));
//		this.asyncEventerWaitRound = new Integer(reader.read(ContainerXML.SELECT_ASYNC_EVENTER_WAIT_ROUND));
//		this.asyncEventerWaitRound = Integer.valueOf(reader.read(ContainerXML.SELECT_ASYNC_EVENTER_WAIT_ROUND));
//		this.asyncEventIdleCheckDelay = new Integer(reader.read(ContainerXML.SELECT_ASYNC_EVENT_IDLE_CHECK_DELAY));
		this.asyncEventIdleCheckDelay = Integer.valueOf(reader.read(ContainerXML.SELECT_ASYNC_EVENT_IDLE_CHECK_DELAY));
//		this.asyncEventIdleCheckPeriod = new Integer(reader.read(ContainerXML.SELECT_ASYNC_EVENT_IDLE_CHECK_PERIOD));
		this.asyncEventIdleCheckPeriod = Integer.valueOf(reader.read(ContainerXML.SELECT_ASYNC_EVENT_IDLE_CHECK_PERIOD));
//		this.schedulerPoolSize = new Integer(reader.read(ContainerXML.SELECT_EVENT_SCHEDULER_POOL_SIZE));
		this.schedulerPoolSize = Integer.valueOf(reader.read(ContainerXML.SELECT_EVENT_SCHEDULER_POOL_SIZE));
//		this.schedulerKeepAliveTime = new Integer(reader.read(ContainerXML.SELECT_EVENT_SCHEDULER_KEEP_ALIVE_TIME));
		this.schedulerKeepAliveTime = Integer.valueOf(reader.read(ContainerXML.SELECT_EVENT_SCHEDULER_KEEP_ALIVE_TIME));

		reader.close();
	}
	
	public void init(String registryIP, int registryPort)
	{
//		ServerProfile.CS().setDefault(false);
		this.registryServerIP = registryIP;
		this.registryServerPort = registryPort;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
	
	public String getRegistryServerIP()
	{
		return this.registryServerIP;
	}
	
	public int getRegistryServerPort()
	{
		return this.registryServerPort;
	}
	
	public boolean isRegistryNeeded()
	{
		return this.isRegistryNeeded;
	}
	
	public int getFreeClientPoolSize()
	{
		return this.freeClientPoolSize;
	}
	
	public int getReaderClientSize()
	{
		return this.readerClientSize;
	}
	
	public long getSyncEventerIdleCheckDelay()
	{
		return this.syncEventerIdleCheckDelay;
	}
	
	public long getSyncEventerIdleCheckPeriod()
	{
		return this.syncEventerIdleCheckPeriod;
	}
	
	public long getSyncEventerMaxIdleTime()
	{
		return this.syncEventerMaxIdleTime;
	}
	
	public int getAsyncEventQueueSize()
	{
		return this.asyncEventQueueSize;
	}
	
	public int getAsyncEventerSize()
	{
		return this.asyncEventerSize;
	}
	
	public long getAsyncEventingWaitTime()
	{
		return this.asyncEventingWaitTime;
	}
	
	public long getAsyncEventQueueWaitTime()
	{
		return this.asyncEventQueueWaitTime;
	}

	/*
	public int getAsyncEventerWaitRound()
	{
		return this.asyncEventerWaitRound;
	}
	*/
	
	public long getAsyncEventIdleCheckDelay()
	{
		return this.asyncEventIdleCheckDelay;
	}
	
	public long getAsyncEventIdleCheckPeriod()
	{
		return this.asyncEventIdleCheckPeriod;
	}
	
	public int getSchedulerPoolSize()
	{
		return this.schedulerPoolSize;
	}
	
	public long getSchedulerKeepAliveTime()
	{
		return this.schedulerKeepAliveTime;
	}
}
