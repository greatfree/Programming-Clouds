package org.greatfree.message.multicast.container;

import org.greatfree.message.ServerMessage;

// Created: 09/14/2020, Bing Li
public abstract class ChildRootResponse extends ServerMessage
{
	private static final long serialVersionUID = -2664914176813403890L;

	public ChildRootResponse(int type)
	{
		super(type);
	}

}
