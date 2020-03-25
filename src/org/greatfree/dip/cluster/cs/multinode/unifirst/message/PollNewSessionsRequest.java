package org.greatfree.dip.cluster.cs.multinode.unifirst.message;

import org.greatfree.dip.cluster.cs.multinode.wurb.message.ChatApplicationID;
import org.greatfree.message.multicast.container.Request;

// Created: 02/10/2019, Bing Li
public class PollNewSessionsRequest extends Request
{
	private static final long serialVersionUID = -2451839264635831681L;

	private String receiverKey;
	private String receiverName;

	public PollNewSessionsRequest(String receiverKey, String receiverName)
	{
//		super(receiverKey, MulticastMessageType.UNICAST_REQUEST, ChatApplicationID.POLL_NEW_SESSIONS_REQUEST);
		super(receiverKey, ChatApplicationID.POLL_NEW_SESSIONS_REQUEST);
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
