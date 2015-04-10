package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * This is the response from the coordinator to the request, IsPublisherExistedRequest. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedResponse extends ServerMessage
{
	private static final long serialVersionUID = -4476671398966834594L;

	// The flag to indicate whether the publisher is existed. 11/29/2014, Bing Li
	private boolean isExisted;

	public IsPublisherExistedResponse(boolean isExisted)
	{
		super(MessageType.IS_PUBLISHER_EXISTED_RESPONSE);
		this.isExisted = isExisted;
	}
	
	public boolean isExisted()
	{
		return this.isExisted;
	}
}
