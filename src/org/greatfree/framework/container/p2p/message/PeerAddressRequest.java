package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Request;

// Created: 01/12/2019, Bing Li
public class PeerAddressRequest extends Request
{
	private static final long serialVersionUID = 6628301008964569718L;

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
