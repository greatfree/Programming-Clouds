package org.greatfree.framework.container.multicast.message;

import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 01/12/2019, Bing Li
public class HelloWorldBroadcastRequest extends ClusterRequest
{
	private static final long serialVersionUID = -3883215146504654020L;

	public HelloWorldBroadcastRequest(int requestType, int applicationID)
	{
		super(requestType, applicationID);
		// TODO Auto-generated constructor stub
	}

}
