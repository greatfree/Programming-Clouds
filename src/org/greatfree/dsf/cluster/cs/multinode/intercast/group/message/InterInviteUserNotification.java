package org.greatfree.dsf.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.InterChildrenNotification;

// Created: 04/19/2019, Bing Li
public class InterInviteUserNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = -1564450265851582739L;

	public InterInviteUserNotification(InviteUserNotification in)
	{
		super(in);
	}

}
