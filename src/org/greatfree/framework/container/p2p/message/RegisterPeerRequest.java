package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Request;

/*
 * Peer disabling information is registered. 03/02/2023, Bing Li
 */

// Created: 01/12/2019, Bing Li
public class RegisterPeerRequest extends Request
{
	private static final long serialVersionUID = -7243484869448178980L;

	private String peerKey;
	private String peerName;
	private String ip;
	private int port;
	private boolean isServerDisabled;
//	private boolean isClientDisabled;
	private boolean isBroker;

	public RegisterPeerRequest(String peerKey, String peerName, String ip, int port)
	{
		super(SystemMessageType.REGISTER_PEER_REQUEST);
		this.peerKey = peerKey;
		this.peerName = peerName;
		this.ip = ip;
		this.port = port;
		this.isServerDisabled = false;
//		this.isClientDisabled = false;
		this.isBroker = false;
	}

//	public RegisterPeerRequest(String peerKey, String peerName, String ip, int port, boolean isSD, boolean isCD)
	public RegisterPeerRequest(String peerKey, String peerName, String ip, int port, boolean isSD)
	{
		super(SystemMessageType.REGISTER_PEER_REQUEST);
		this.peerKey = peerKey;
		this.peerName = peerName;
		this.ip = ip;
		this.port = port;
		this.isServerDisabled = isSD;
//		this.isClientDisabled = isCD;
		this.isBroker = false;
	}

	public RegisterPeerRequest(String peerKey, String peerName, boolean isBroker, String ip, int port)
	{
		super(SystemMessageType.REGISTER_PEER_REQUEST);
		this.peerKey = peerKey;
		this.peerName = peerName;
		this.ip = ip;
		this.port = port;
		this.isServerDisabled = false;
		this.isBroker = isBroker;
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

	public boolean isServerDisabled()
	{
		return this.isServerDisabled;
	}

	/*
	public boolean isClientDisabled()
	{
		return this.isClientDisabled;
	}
	*/
	
	public boolean isBroker()
	{
		return this.isBroker;
	}
}
