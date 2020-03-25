package org.greatfree.dip.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.InterChildrenNotification;

// Created: 04/20/2019, Bing Li
public class InterRemoveUserNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = 5791230089579409680L;

	public InterRemoveUserNotification(RemoveUserNotification in)
	{
		super(in);
	}

}
