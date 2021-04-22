package org.greatfree.framework.cluster.cs.multinode.intercast.message;

import org.greatfree.framework.cluster.cs.multinode.wurb.message.ChatApplicationID;
import org.greatfree.message.multicast.container.IntercastRequest;

// Created: 02/25/2019, Bing Li
public class ChatPartnerRequest extends IntercastRequest
{
	private static final long serialVersionUID = -7247856298263216294L;
	
	private String localUserKey;
	private String userKey;

	public ChatPartnerRequest(String localUserKey, String userKey)
	{
		super(localUserKey, userKey, ChatApplicationID.CHAT_PARTNER_REQUEST);
		this.localUserKey = localUserKey;
		this.userKey = userKey;
	}
	
	public String getLocalUserKey()
	{
		return this.localUserKey;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
}
