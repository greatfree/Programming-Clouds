package org.greatfree.dip.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.InterChildrenRequest;

// Created: 04/20/2019, Bing Li
public class InterGroupMembersRequest extends InterChildrenRequest
{
	private static final long serialVersionUID = 1741777143930303907L;

	public InterGroupMembersRequest(GroupMembersRequest ir)
	{
		super(ir);
	}
}
