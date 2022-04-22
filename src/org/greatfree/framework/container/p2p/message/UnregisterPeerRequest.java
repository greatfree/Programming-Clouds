package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Request;

// Created: 01/12/2018, Bing Li
public class UnregisterPeerRequest extends Request
{
	private static final long serialVersionUID = 5045329884178054786L;

	private String peerKey;

	public UnregisterPeerRequest(String peerKey)
	{
		super(SystemMessageType.UNREGISTER_PEER_REQUEST);
		this.peerKey = peerKey;
	}

	public String getPeerKey()
	{
		return this.peerKey;
	}
}
