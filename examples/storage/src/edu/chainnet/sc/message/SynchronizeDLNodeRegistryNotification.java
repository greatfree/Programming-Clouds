package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Notification;

/*
 * When nodes information is updated, the notification is used to synchronize that between the chains and the collaborator. 10/18/2020, Bing Li
 */

// Created: 10/18/2020, Bing Li
public class SynchronizeDLNodeRegistryNotification extends Notification
{
	private static final long serialVersionUID = -7741240497575940713L;
	
	private DSNode node;

	public SynchronizeDLNodeRegistryNotification(DSNode node)
	{
		super(node.getKey(), SCAppID.SYNCHRONIZE_DATA_LAKE_NODE_REGISTRY_NOTIFICATION);
		this.node = node;
	}

	public DSNode getNode()
	{
		return this.node;
	}
}
