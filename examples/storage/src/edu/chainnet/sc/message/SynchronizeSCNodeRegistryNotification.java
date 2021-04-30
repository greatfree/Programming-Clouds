package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Notification;

/*
 * When nodes information is updated, the notification is used to synchronize that between the chains and the collaborator. 10/18/2020, Bing Li
 */

// Created: 10/18/2020, Bing Li
public class SynchronizeSCNodeRegistryNotification extends Notification
{
	private static final long serialVersionUID = 4790744561860206024L;

	private DSNode node;

	public SynchronizeSCNodeRegistryNotification(DSNode node)
	{
		super(node.getKey(), SCAppID.SYNCHRONIZE_SMART_CONTRACT_NODE_REGISTRY_NOTIFICATION);
		this.node = node;
	}

	public DSNode getNode()
	{
		return this.node;
	}
}
