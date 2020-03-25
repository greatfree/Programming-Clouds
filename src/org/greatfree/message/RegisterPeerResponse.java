package org.greatfree.message;

/*
 * The response is responded from a registry server to a peer to tell it an existing port on the local node. It is possible that the local node has more than one peers. If so, their ports should not conflict. 05/01/2017, Bing Li
 */

// Created: 05/01/2017, Bing Li
public class RegisterPeerResponse extends ServerMessage
{
	private static final long serialVersionUID = -7028585042815549825L;
	
	private int peerPort;
//	private int adminPort;

//	public RegisterPeerResponse(int peerPort, int adminPort)
	public RegisterPeerResponse(int peerPort)
	{
		super(SystemMessageType.REGISTER_PEER_RESPONSE);
		this.peerPort = peerPort;
//		this.adminPort = adminPort;
	}

	public int getPeerPort()
	{
		return this.peerPort;
	}

	/*
	public int getAdminPort()
	{
		return this.adminPort;
	}
	*/
}
