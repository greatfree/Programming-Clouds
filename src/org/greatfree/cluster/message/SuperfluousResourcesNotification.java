package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

/*
 * When superfluous computing resources are available in a cluster, some children need to be returned from the task cluster to the pool cluster. The message is responsible for notifying the task cluster. 09/06/2020, Bing Li
 */

// Created: 09/06/2020, Bing Li
public class SuperfluousResourcesNotification extends ServerMessage
{
	private static final long serialVersionUID = -8368497541688093738L;
	
	private String poolClusterRootKey;
	private IPAddress poolClusterRootIP;

	public SuperfluousResourcesNotification(String poolClusterRootKey, IPAddress poolClusterRootIP)
	{
		super(ClusterMessageType.SUPERFLUOUS_RESOURCES_NOTIFICATION);
		this.poolClusterRootKey = poolClusterRootKey;
		this.poolClusterRootIP = poolClusterRootIP;
	}
	
	public String getPoolClusterRootKey()
	{
		return this.poolClusterRootKey;
	}
	
	public IPAddress getPoolClusterRootIP()
	{
		return this.poolClusterRootIP;
	}
}
