package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

// Created: 12/10/2016, Bing Li
public class TestRequest extends ServerMessage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8727719427732353149L;
	
	private String request;

	public TestRequest(String request)
	{
		super(MessageType.TEST_REQUEST);
		this.request = request;
	}

	public String getRequest()
	{
		return this.request;
	}
}
