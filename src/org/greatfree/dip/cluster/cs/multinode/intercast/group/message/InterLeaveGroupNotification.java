package org.greatfree.dip.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.InterChildrenNotification;

// Created: 04/20/2019, Bing Li
public class InterLeaveGroupNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = -4027708126565390211L;

	public InterLeaveGroupNotification(LeaveGroupNotification in)
	{
		super(in);
	}

}
