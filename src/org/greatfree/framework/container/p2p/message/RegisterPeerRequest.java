package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Request;

// Created: 01/12/2019, Bing Li
public class RegisterPeerRequest extends Request
{
	private static final long serialVersionUID = -7243484869448178980L;

	private String peerKey;
	private String peerName;
	private String ip;
	private int port;

	public RegisterPeerRequest(String peerKey, String peerName, String ip, int port)
	{
		super(SystemMessageType.REGISTER_PEER_REQUEST);
		this.peerKey = peerKey;
		this.peerName = peerName;
		this.ip = ip;
		this.port = port;
	}
	
	public String getPeerKey()
	{
		return this.peerKey;
	}
	
	public String getPeerName()
	{
		return this.peerName;
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
