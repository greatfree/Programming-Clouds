package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

// Created: 10/09/2018, Bing Li
public class IsRootOnlineResponse extends ServerMessage
{
	private static final long serialVersionUID = -5518698417202072257L;

	private IPAddress rootAddress;
	private boolean isOnline;

	public IsRootOnlineResponse(IPAddress rootAddress, boolean isOnline)
	{
		super(ClusterMessageType.IS_ROOT_ONLINE_RESPONSE);
		this.rootAddress = rootAddress;
		this.isOnline = isOnline;
	}
	
	public IPAddress getRootAddress()
	{
		return this.rootAddress;
	}

	public boolean isOnline()
	{
		return this.isOnline;
	}
}
