package edu.greatfree.container.cs.multinode.message;

import org.greatfree.message.container.Notification;

// Created: 01/06/2019, Bing Li
public class ChatNotification extends Notification
{
	private static final long serialVersionUID = 1478587392850307060L;

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
		super(ChatApplicationID.CHAT_NOTIFICATION);
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
