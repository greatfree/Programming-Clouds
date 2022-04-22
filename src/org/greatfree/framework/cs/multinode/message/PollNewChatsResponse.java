package org.greatfree.framework.cs.multinode.message;

import java.util.List;

import org.greatfree.chat.ChatMessage;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

/*
 * If new chatting messages are available, they should be returned to the request sender. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
public class PollNewChatsResponse extends ServerMessage
{
	private static final long serialVersionUID = -4869442313781358479L;
	
//	private Collection<ChatMessage> messages;
	private List<ChatMessage> messages;
//	private Map<String, ChatMessage> messages;
//	private List<String> messages;
//	private String messages;

//	public PollNewChatsResponse(Collection<ChatMessage> messages)
//	public PollNewChatsResponse(List<String> messages)
//	public PollNewChatsResponse(String messages)
//	public PollNewChatsResponse(Map<String, ChatMessage> messages)
	public PollNewChatsResponse(List<ChatMessage> messages)
	{
		super(SystemMessageType.POLL_NEW_CHATS_RESPONSE);
		this.messages = messages;
	}

//	public Collection<ChatMessage> getMessages()
//	public Map<String, ChatMessage> getMessages()
//	public String getMessages()
//	public List<String> getMessages()
	public List<ChatMessage> getMessages()
	{
		return this.messages;
	}
}
