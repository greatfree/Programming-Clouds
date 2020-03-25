package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

// Created: 12/09/2016, Bing Li
public class TestNotification extends ServerMessage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6936158947185462689L;
	
	private String message;

	public TestNotification(String message)
	{
		super(MessageType.TEST_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
