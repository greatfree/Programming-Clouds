package org.greatfree.cluster.message;

import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.util.IPAddress;

// Created: 09/06/2020, Bing Li
public class SelectedChildNotification extends MulticastMessage
{
	private static final long serialVersionUID = -3217627295663136280L;

	private String rootKey;
	private IPAddress clusterRootIP;

	public SelectedChildNotification(String rootKey, IPAddress ip)
	{
		super(ClusterMessageType.SELECTED_CHILD_NOTIFICATION);
		this.rootKey = rootKey;
		this.clusterRootIP = ip;
	}
	
	public String getRootKey()
	{
		return this.rootKey;
	}
	
	public IPAddress getClusterRootIP()
	{
		return this.clusterRootIP;
	}
}
