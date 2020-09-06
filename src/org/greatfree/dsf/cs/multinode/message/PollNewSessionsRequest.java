package org.greatfree.dsf.cs.multinode.message;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.message.ServerMessage;

/*
 * The request is sent to the chatting server to check whether new sessions are available to the request sender periodically. 04/24/2017, Bing Li
 */

// Created: 04/24/2017, Bing Li
public class PollNewSessionsRequest extends ServerMessage
{
	private static final long serialVersionUID = 6979466184161327628L;

	private String receiverKey;
	private String username;

	public PollNewSessionsRequest(String receiverKey, String username)
	{
		super(ChatMessageType.POLL_NEW_SESSIONS_REQUEST);
		this.receiverKey = receiverKey;
		this.username = username;
	}

	public String getReceiverKey()
	{
		return this.receiverKey;
	}
	
	public String getUsername()
	{
		return this.username;
	}
}
