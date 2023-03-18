package org.greatfree.message;

import org.greatfree.util.UtilConfig;

/*
 * The response is responded from a registry server to a peer to tell it an existing port on the local node. It is possible that the local node has more than one peers. If so, their ports should not conflict. 05/01/2017, Bing Li
 */

// Created: 05/01/2017, Bing Li
public class RegisterPeerResponse extends ServerMessage
{
//	private final static Logger log = Logger.getLogger("org.greatfree.message");

	private static final long serialVersionUID = -7028585042815549825L;
	
	private int peerPort;
	private boolean isDuplicate;
//	private int adminPort;

//	public RegisterPeerResponse(int peerPort, int adminPort)
	public RegisterPeerResponse(int peerPort)
	{
		super(SystemMessageType.REGISTER_PEER_RESPONSE);
		this.peerPort = peerPort;
//		this.adminPort = adminPort;
		this.isDuplicate = false;
//		log.info("1) port = " + peerPort + ", isDuplicated = " + this.isDuplicate);
	}
	
	public RegisterPeerResponse(boolean isDuplicate)
	{
		super(SystemMessageType.REGISTER_PEER_RESPONSE);
		this.peerPort = UtilConfig.NO_PORT;
		this.isDuplicate = isDuplicate;
//		log.info("2) port = " + peerPort + ", isDuplicated = " + this.isDuplicate);
	}

	public int getPeerPort()
	{
		return this.peerPort;
	}
	
	public boolean isDuplicate()
	{
		return this.isDuplicate;
	}

	/*
	public int getAdminPort()
	{
		return this.adminPort;
	}
	*/
}
