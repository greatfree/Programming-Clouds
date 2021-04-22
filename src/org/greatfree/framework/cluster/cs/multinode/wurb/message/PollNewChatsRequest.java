package org.greatfree.framework.cluster.cs.multinode.wurb.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

/*
 * The counterpart, PollNewChatsResponse, is the one org.greatfree.chat.message.cs.PollNewChatsResponse. It is a reused one. 01/27/2019, Bing Li
 */

// Created: 01/27/2019, Bing Li
public class PollNewChatsRequest extends Request
{
	private static final long serialVersionUID = 7803751537905084693L;
	
	// The chat session to be checked. 04/24/2017, Bing Li
	private String chatSessionKey;
	// The chat messages' receiver. 04/24/2017, Bing Li
	private String receiverKey;
	// The username of the receiver. 04/27/2017, Bing Li
	private String receiverName;

	public PollNewChatsRequest(String chatSessionKey, String receiverKey, String receiverName)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, ChatApplicationID.POLL_NEW_CHATS_REQUEST);
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
