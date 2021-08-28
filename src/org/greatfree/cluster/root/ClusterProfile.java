package org.greatfree.cluster.root;

import org.greatfree.server.container.ContainerXML;
import org.greatfree.server.container.PeerProfile;
import org.greatfree.server.container.ServerProfile;
import org.greatfree.util.Tools;
import org.greatfree.util.XPathOnDiskReader;

// Created: 01/13/2019, Bing Li
public class ClusterProfile
{
	private int schedulerPoolSize;
	private long schedulerKeepAliveTime;
	private long schedulerShutdownTimeout;
	private int rootBranchCount;
	private int subBranchCount;
	private long broadcastRequestWaitTime;
	private String rootKey;
	
	private ClusterProfile()
	{
		ServerProfile.CS().setDefault(true);
	}
	
	private static ClusterProfile instance = new ClusterProfile();
	
	public static ClusterProfile CLUSTER()
	{
		if (instance == null)
		{
			instance = new ClusterProfile();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void init(String path)
	{
		ServerProfile.CS().init(path);
		PeerProfile.P2P().init(path);

		XPathOnDiskReader reader = new XPathOnDiskReader(path, true);
		this.schedulerPoolSize = new Integer(reader.read(ContainerXML.SELECT_SCHEDULER_POOL_SIZE));
		this.schedulerKeepAliveTime = new Integer(reader.read(ContainerXML.SELECT_SCHEDULER_KEEP_ALIVE_TIME));
		this.schedulerShutdownTimeout = new Integer(reader.read(ContainerXML.SELECT_SCHEDULER_SHUTDOWN_TIME_OUT));
		this.rootBranchCount = new Integer(reader.read(ContainerXML.SELECT_ROOT_BRANCH_COUNT));
		this.subBranchCount = new Integer(reader.read(ContainerXML.SELECT_SUB_BRANCH_COUNT));
		this.broadcastRequestWaitTime = new Integer(reader.read(ContainerXML.SELECT_BROADCAST_REQUEST_WAIT_TIME));
		this.rootKey = Tools.getHash(reader.read(ContainerXML.SELECT_ROOT_NAME));
		
		reader.close();
	}
	
	public void init(String registryIP, int registryPort)
	{
		ServerProfile.CS().setDefault(false);
		PeerProfile.P2P().init(registryIP, registryPort);
	}
	
	public int getSchedulerPoolSize()
	{
		return this.schedulerPoolSize;
	}
	
	public long getSchedulerKeepAliveTime()
	{
		return this.schedulerKeepAliveTime;
	}
	
	public long getSchedulerShutdownTimeout()
	{
		return this.schedulerShutdownTimeout;
	}
	
	public int getRootBranchCount()
	{
		return this.rootBranchCount;
	}

	public int getSubBranchCount()
	{
		return this.subBranchCount;
	}
	
	public long getBroadcastRequestWaitTime()
	{
		return this.broadcastRequestWaitTime;
	}
	
	public String getRootKey()
	{
		return this.rootKey;
	}
}
