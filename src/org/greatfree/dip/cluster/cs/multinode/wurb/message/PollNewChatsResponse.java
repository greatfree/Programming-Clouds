package org.greatfree.dip.cluster.cs.multinode.wurb.message;

import java.util.List;

import org.greatfree.chat.ChatMessage;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 01/28/2019, Bing Li
public class PollNewChatsResponse extends MulticastResponse
{
	private static final long serialVersionUID = 8606125108871326612L;

	private List<ChatMessage> messages;

	public PollNewChatsResponse(List<ChatMessage> messages, String collaboratorKey)
	{
		super(ChatApplicationID.POLL_NEW_CHATS_RESPONSE, collaboratorKey);
		this.messages = messages;
	}

	public List<ChatMessage> getMessages()
	{
		return this.messages;
	}
}
