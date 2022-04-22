package org.greatfree.framework.p2p.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

/*
 * The response contains the account description, especially the IP address for the Peer chatting system. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
public class ChatPartnerResponse extends ServerMessage
{
	private static final long serialVersionUID = 8082301869328779879L;
	
	private String userKey;
	private String userName;
	private String description;
	private String preference;
	private String ip;
	private int port;

	public ChatPartnerResponse(String userKey, String userName, String description, String preference, String ip, int port)
	{
		super(SystemMessageType.PEER_CHAT_PARTNER_RESPONSE);
		this.userKey = userKey;
		this.userName = userName;
		this.description = description;
		this.preference = preference;
		this.ip = ip;
		this.port = port;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public String getPreference()
	{
		return this.preference;
	}
	
	public String getIP()
	{
		return this.ip;
	}
	
	public int getPort()
	{
		return this.port;
	}
}
