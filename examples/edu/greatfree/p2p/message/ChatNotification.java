package edu.greatfree.p2p.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.UtilConfig;

import edu.greatfree.cs.multinode.message.ChatMessageType;

// Created: 05/02/2017, Bing Li
public class ChatNotification extends ServerMessage
{
	private static final long serialVersionUID = -4180547758940870394L;
	
	// The chatting message. 04/27/2017, Bing Li
	private String message;
	// The sender name. 04/27/2017, Bing Li
	private String senderName;

	public ChatNotification(String message, String senderName)
	{
		super(ChatMessageType.PEER_CHAT_NOTIFICATION);
		this.message = message;
		this.senderName = senderName;
	}
	
	public ChatNotification(String message)
	{
		super(ChatMessageType.PEER_CHAT_NOTIFICATION);
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
