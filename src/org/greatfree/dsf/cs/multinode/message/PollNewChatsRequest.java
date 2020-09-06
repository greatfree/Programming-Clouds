package org.greatfree.dsf.cs.multinode.message;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.message.ServerMessage;

/*
 * The request is used to check the chatting server whether new chatting messages are available. It is sent to the chatting server periodically. If so, the new messages must be returned to the local user. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
public class PollNewChatsRequest extends ServerMessage
{
	private static final long serialVersionUID = -6869452383641427790L;
	
	// The chat session to be checked. 04/24/2017, Bing Li
	private String chatSessionKey;
	// The chat messages' receiver. 04/24/2017, Bing Li
	private String receiverKey;
	// The username of the receiver. 04/27/2017, Bing Li
	private String receiverName;

	public PollNewChatsRequest(String chatSessionKey, String receiverKey, String receiverName)
	{
		super(ChatMessageType.POLL_NEW_CHATS_REQUEST);
		this.chatSessionKey = chatSessionKey;
		this.receiverKey = receiverKey;
		this.receiverName = receiverName;
	}

	public String getChatSessionKey()
	{
		return this.chatSessionKey;
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
