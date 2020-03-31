package edu.greatfree.container.cs.multinode.message;

import org.greatfree.message.container.Request;

// Created: 01/06/2018, Bing Li
public class PollNewChatsRequest extends Request
{
	private static final long serialVersionUID = -7791451794857369213L;

	// The chat session to be checked. 04/24/2017, Bing Li
	private String chatSessionKey;
	// The chat messages' receiver. 04/24/2017, Bing Li
	private String receiverKey;
	// The username of the receiver. 04/27/2017, Bing Li
	private String username;

	public PollNewChatsRequest(String chatSessionKey, String receiverKey, String username)
	{
		super(ChatApplicationID.POLL_NEW_CHATS_REQUEST);
		this.chatSessionKey = chatSessionKey;
		this.receiverKey = receiverKey;
		this.username = username;
	}

	public String getChatSessionKey()
	{
		return this.chatSessionKey;
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
