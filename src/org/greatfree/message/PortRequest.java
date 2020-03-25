package org.greatfree.message;

/*
 * Since it is possible to get port conflicts, which happen when running multiple peers on a single node, it is necessary to send the request to the registry server to get an idle port. 05/02/2017, Bing Li
 */

// Created: 05/02/2017, Bing Li
public class PortRequest extends ServerMessage
{
	private static final long serialVersionUID = -1112030214169918144L;

	private String peerKey;
	private String portKey;
	private String ip;
	private int currentPort;

	public PortRequest(String peerKey, String portKey, String ip, int currentPort)
	{
		super(SystemMessageType.PORT_REQUEST);
		this.peerKey = peerKey;
		this.portKey = portKey;
		this.ip = ip;
		this.currentPort = currentPort;
	}
	
	public String getPeerKey()
	{
		return this.peerKey;
	}

	public String getPortKey()
	{
		return this.portKey;
	}
	
	public String getIP()
	{
		return this.ip;
	}
	
	public int getCurrentPort()
	{
		return this.currentPort;
	}
}
