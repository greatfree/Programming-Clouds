package org.greatfree.dip.container.p2p.message;

import org.greatfree.message.container.Request;

// Created: 01/12/2019, Bing Li
public class PortRequest extends Request
{
	private static final long serialVersionUID = -2442386103243810665L;

	private String peerKey;
	private String portKey;
	private String ip;
	private int currentPort;

	public PortRequest(String peerKey, String portKey, String ip, int currentPort)
	{
		super(P2PChatApplicationID.PORT_REQUEST);
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
