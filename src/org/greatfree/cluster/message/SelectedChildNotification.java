package org.greatfree.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.util.IPAddress;

/*
 * When a child in the pool is selected to join a task cluster, it receives such a message. Another case is that a child is asked to leave for a pool cluster from a task cluster. The notification is received as well. 09/11/2020, Bing Li
 */

// Created: 09/06/2020, Bing Li
// public class SelectedChildNotification extends MulticastNotification
public class SelectedChildNotification extends Notification
{
	private static final long serialVersionUID = -3217627295663136280L;

	private String rootKey;
	private IPAddress clusterRootIP;
	private boolean isBusy;

	public SelectedChildNotification(String rootKey, IPAddress ip, boolean isBusy)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ClusterMessageType.SELECTED_CHILD_NOTIFICATION);
		this.rootKey = rootKey;
		this.clusterRootIP = ip;
		this.isBusy = isBusy;
	}
	
	public String getRootKey()
	{
		return this.rootKey;
	}
	
	public IPAddress getClusterRootIP()
	{
		return this.clusterRootIP;
	}
	
	public boolean isBusy()
	{
		return this.isBusy;
	}
}
