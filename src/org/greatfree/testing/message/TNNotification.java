package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

// Created: 04/10/2020, Bing Li
public class TNNotification extends ServerMessage
{
	private static final long serialVersionUID = -7975098317532170308L;
	
	private String msg;

	public TNNotification(String msg)
	{
		super(MessageType.TN_NOTIFICATION);
		this.msg = msg;
	}

	public String getMessage()
	{
		return this.msg;
	}
}
