package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Notification;

/*
 * When nodes information is updated, the notification is used to synchronize that between the chains and the collaborator. 10/18/2020, Bing Li
 */

// Created: 10/18/2020, Bing Li
public class SynchronizeONNodeRegistryNotification extends Notification
{
	private static final long serialVersionUID = -4182834627244077510L;

	private DSNode node;

	public SynchronizeONNodeRegistryNotification(DSNode node)
	{
		super(node.getKey(), SCAppID.SYNCHRONIZE_ORACLE_NODE_REGISTRY_NOTIFICATION);
		this.node = node;
	}

	public DSNode getNode()
	{
		return this.node;
	}
}
