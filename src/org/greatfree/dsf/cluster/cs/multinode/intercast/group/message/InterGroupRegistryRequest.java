package org.greatfree.dsf.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.InterChildrenRequest;

// Created: 04/25/2019, Bing Li
public class InterGroupRegistryRequest extends InterChildrenRequest
{
	private static final long serialVersionUID = -1523550729261630197L;

	public InterGroupRegistryRequest(GroupRegistryRequest ir)
	{
		super(ir);
	}

}
