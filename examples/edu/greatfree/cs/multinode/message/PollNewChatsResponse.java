package edu.greatfree.cs.multinode.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

import edu.greatfree.container.cs.multinode.message.ChatMessage;

/*
 * If new chatting messages are available, they should be returned to the request sender. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
public class PollNewChatsResponse extends ServerMessage
{
	private static final long serialVersionUID = -4869442313781358479L;
	
	private List<ChatMessage> messages;

	public PollNewChatsResponse(List<ChatMessage> messages)
	{
		super(ChatMessageType.POLL_NEW_CHATS_RESPONSE);
		this.messages = messages;
	}

	public List<ChatMessage> getMessages()
	{
		return this.messages;
	}
}
