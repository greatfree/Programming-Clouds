package org.greatfree.dsf.cluster.cs.multinode.intercast.message;

import org.greatfree.message.multicast.container.InterChildrenRequest;

// Created: 02/26/2019, Bing Li
public class InterChatPartnerRequest extends InterChildrenRequest
{
	private static final long serialVersionUID = 3880915941350975819L;
	
//	private String userKey;
	
//	private ChatPartnerRequest cpr;

//	public InterChatPartnerRequest(String userKey, ChatPartnerRequest cpr)
//	public InterChatPartnerRequest(String ip, int port, ChatPartnerRequest cpr)
	public InterChatPartnerRequest(ChatPartnerRequest cpr)
	{
//		super(userKey, ChatApplicationID.CHAT_PARTNER_REQUEST, cpr);
//		super(ip, port, cpr);
		super(cpr);
	}

	/*
	public String getUserKey()
	{
		return this.userKey;
	}
	*/

	/*
	public ChatPartnerRequest getChatPartnerRequest()
	{
		return this.cpr;
	}
	*/
}
