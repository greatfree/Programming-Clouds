package org.greatfree.framework.cluster.cs.multinode.unifirst.message;

import org.greatfree.framework.cluster.cs.multinode.wurb.message.ChatApplicationID;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 02/10/2019, Bing Li
public class PollNewChatsRequest extends ClusterRequest
{
	private static final long serialVersionUID = 3159807841912402642L;

	// The chat session to be checked. 04/24/2017, Bing Li
	private String chatSessionKey;
	// The chat messages' receiver. 04/24/2017, Bing Li
	private String receiverKey;
	// The username of the receiver. 04/27/2017, Bing Li
	private String receiverName;

	public PollNewChatsRequest(String chatSessionKey, String receiverKey, String receiverName)
	{
//		super(receiverKey, MulticastMessageType.UNICAST_REQUEST, ChatApplicationID.POLL_NEW_CHATS_REQUEST);
		super(receiverKey, ChatApplicationID.POLL_NEW_CHATS_REQUEST);
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
