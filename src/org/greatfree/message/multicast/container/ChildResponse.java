package org.greatfree.message.multicast.container;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/02/2018, Bing Li
public class ChildResponse extends ServerMessage
{
	private static final long serialVersionUID = -5004187798822759536L;
	
	private MulticastResponse response;

	public ChildResponse(MulticastResponse response)
	{
		super(ClusterMessageType.CHILD_RESPONSE);
		this.response = response;
	}

	public MulticastResponse getResponse()
	{
		return this.response;
	}
}
