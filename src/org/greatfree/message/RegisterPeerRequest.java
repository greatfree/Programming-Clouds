package org.greatfree.message;

/*
 * The request is sent from a peer to the registry server to obtain an available port on the local node. It is possible that the local node has more than one peers. If so, their ports should not conflict. 05/01/2017, Bing Li
 */

// Created: 05/01/2017, Bing Li
public class RegisterPeerRequest extends ServerMessage
{
	private static final long serialVersionUID = 1159700115385713220L;

	private String peerKey;
	private String peerName;
	private String ip;
	private int port;
//	private int serverCount;

//	public RegisterPeerRequest(String peerKey, String peerName, String ip, int port, int serverCount)
	public RegisterPeerRequest(String peerKey, String peerName, String ip, int port)
	{
		super(SystemMessageType.REGISTER_PEER_REQUEST);
		this.peerKey = peerKey;
		this.peerName = peerName;
		this.ip = ip;
		this.port = port;
//		this.serverCount = serverCount;
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

	/*
	public int getServerCount()
	{
		return this.serverCount;
	}
	*/
}
