package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

/*
 * The notification is sent from a system workload monitor to notify the pool cluster. Then, the child which receives the message is able to leave the pool cluster and join the task cluster to alleviate its workload pressure. 09/05/2020, Bing Li
 */

// Created: 09/05/2020, Bing Li
// public class HeavyWorkloadNotification extends Notification
public class HeavyWorkloadNotification extends ServerMessage
{
	private static final long serialVersionUID = -7874315468225099031L;

	private String taskClusterRootKey;
	private IPAddress taskClusterRootIP;
	private int nodeSize;
	
	public HeavyWorkloadNotification(String taskClusterRootKey, IPAddress taskClusterRootIP, int nodeSize)
	{
		super(ClusterMessageType.HEAVY_WORKLOAD_NOTIFICATION);
		this.taskClusterRootKey = taskClusterRootKey;
		this.taskClusterRootIP = taskClusterRootIP;
		this.nodeSize = nodeSize;
	}
	
	public String getTaskClusterRootKey()
	{
		return this.taskClusterRootKey;
	}
	
	public IPAddress getTaskClusterRootIP()
	{
		return this.taskClusterRootIP;
	}
	
	public int getNodeSize()
	{
		return this.nodeSize;
	}
}
