package org.greatfree.framework.container.cs.twonode.message;

import org.greatfree.message.container.Notification;

// Created: 12/18/2018, Bing Li
public class ChatNotification extends Notification
{
	private static final long serialVersionUID = -4964832826438570070L;
	
	// The chatting session key. 04/27/2017, Bing Li
	private String sessionKey;
	// The chatting message. 04/27/2017, Bing Li
	private String message;
	// The sender name. 04/27/2017, Bing Li
	private String senderName;
	// The sender key. 04/27/2017, Bing Li
	private String senderKey;
	// The receiver name. 04/27/2017, Bing Li
	private String receiverName;
	// The receiver key. 04/27/2017, Bing Li
	private String receiverKey;

	public ChatNotification(String sessionKey, String message, String senderKey, String senderName, String receiverKey, String receiverName)
	{
//		super(CSMessageType.NOTIFICATION, ApplicationID.CHAT_NOTIFICATION);
		super(ApplicationID.CHAT_NOTIFICATION);
		this.sessionKey = sessionKey;
		this.message = message;
		this.senderKey = senderKey;
		this.senderName = senderName;
		this.receiverKey = receiverKey;
		this.receiverName = receiverName;
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}

	public String getMessage()
	{
		return this.message;
	}
	
	public String getSenderKey()
	{
		return this.senderKey;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
	
	public String getReceiverKey()
	{
		return this.receiverKey;
	}
	
	public String getReceiverName()
	{
		return this.receiverName;
	}
}
