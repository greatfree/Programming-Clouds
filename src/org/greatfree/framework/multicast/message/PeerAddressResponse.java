package org.greatfree.framework.multicast.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.util.IPAddress;

// Created: 09/11/2018, Bing Li
public class PeerAddressResponse extends ServerMessage
{
	private static final long serialVersionUID = 6682424095125545615L;
	
	private IPAddress peerAddress;

	public PeerAddressResponse(IPAddress peerAddress)
	{
		super(SystemMessageType.PEER_ADDRESS_RESPONSE);
		this.peerAddress = peerAddress;
	}

	public IPAddress getPeerAddress()
	{
		return this.peerAddress;
	}
}
