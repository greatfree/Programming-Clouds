package org.greatfree.framework.cluster.cs.multinode.wurb.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 01/26/2019, Bing Li
public class PollNewSessionsRequest extends ClusterRequest
{
	private static final long serialVersionUID = -5253945742205950117L;

	private String receiverKey;
	private String receiverName;

	public PollNewSessionsRequest(String receiverKey, String receiverName)
	{
		/*
		 * Since chatting users are registered in the way of unicasting, it is required to poll new sessions through broadcasting.
		 */
		super(MulticastMessageType.BROADCAST_REQUEST, ChatApplicationID.POLL_NEW_SESSIONS_REQUEST);
		this.receiverKey = receiverKey;
		this.receiverName = receiverName;
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
