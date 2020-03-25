package org.greatfree.message;

/*
 * The response is responded from a registry server to a peer to ensure whether it is succeeded to unregister from the registry server or not. 05/20/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
public class UnregisterPeerResponse extends ServerMessage
{
	private static final long serialVersionUID = 7449858374990299522L;
	
	private boolean isSucceeded;

	public UnregisterPeerResponse(boolean isSucceeded)
	{
		super(SystemMessageType.UNREGISTER_PEER_RESPONSE);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
