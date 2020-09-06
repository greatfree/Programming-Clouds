package org.greatfree.dsf.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.InterChildrenNotification;

// Created: 04/20/2019, Bing Li
public class InterGroupChatNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = 1251810671841112643L;

	public InterGroupChatNotification(GroupChatNotification in)
	{
		super(in);
	}
}
