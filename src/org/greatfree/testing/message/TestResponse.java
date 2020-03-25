package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

// Created: 12/10/2016, Bing Li
public class TestResponse extends ServerMessage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4141489337639907434L;
	
	private String response;

	public TestResponse(String response)
	{
		super(MessageType.TEST_RESPONSE);
		this.response = response;
	}

	public String getResponse()
	{
		return this.response;
	}
}
