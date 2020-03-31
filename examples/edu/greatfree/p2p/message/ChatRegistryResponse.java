package edu.greatfree.p2p.message;

import org.greatfree.message.ServerMessage;

import edu.greatfree.cs.multinode.message.ChatMessageType;

/*
 * The response is used to respond to the client node about the result of the account registry. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
public class ChatRegistryResponse extends ServerMessage
{
	private static final long serialVersionUID = -7714754232981283178L;

	// The result of one account's registry. 04/15/2017, Bing Li
	private boolean isSucceeded;

	public ChatRegistryResponse(boolean isSucceeded)
	{
		super(ChatMessageType.PEER_CHAT_REGISTRY_RESPONSE);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
