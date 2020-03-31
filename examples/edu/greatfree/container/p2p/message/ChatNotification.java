package edu.greatfree.container.p2p.message;

import org.greatfree.message.container.Notification;
import org.greatfree.util.UtilConfig;

// Created: 01/12/2019, Bing Li
public class ChatNotification extends Notification
{
	private static final long serialVersionUID = -5624240621989402883L;

	// The chatting message. 04/27/2017, Bing Li
	private String message;
	// The sender name. 04/27/2017, Bing Li
	private String senderName;

	public ChatNotification(String message, String senderName)
	{
		super(P2PChatApplicationID.CHAT_NOTIFICATION);
		this.message = message;
		this.senderName = senderName;
	}

	public ChatNotification(String message)
	{
		super(P2PChatApplicationID.CHAT_NOTIFICATION);
		this.message = message;
		this.senderName = UtilConfig.EMPTY_STRING;
	}

	public String getMessage()
	{
		return this.message;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
}
