package org.greatfree.message;

/*
 * The request is sent from a peer to the registry server to unregister itself. It happens when the peer leaves the system. 05/01/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
public class UnregisterPeerRequest extends ServerMessage
{
	private static final long serialVersionUID = -7121842763115805356L;
	
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
