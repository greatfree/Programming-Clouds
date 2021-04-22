package org.greatfree.framework.multicast.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

// Created: 09/11/2018, Bing Li
public class PeerAddressRequest extends ServerMessage
{
	private static final long serialVersionUID = -7421985186852423654L;
	
	private String peerID;

	public PeerAddressRequest(String peerID)
	{
		super(SystemMessageType.PEER_ADDRESS_REQUEST);
		this.peerID = peerID;
	}

	public String getPeerID()
	{
		return this.peerID;
	}
}
