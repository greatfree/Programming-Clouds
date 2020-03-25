package org.greatfree.message;

/*
 * Since it is possible to get port conflicts, which happen when running multiple peers on a single node, it is necessary to send the request to the registry server to get an idle port. The response encloses the idle port to the requester. 05/02/2017, Bing Li
 */

// Created: 05/02/2017, Bing Li
public class PortResponse extends ServerMessage
{
	private static final long serialVersionUID = -1214644579199253066L;
	
	private int port;

	public PortResponse(int port)
	{
		super(SystemMessageType.PORT_RESPONSE);
		this.port = port;
	}

	public int getPort()
	{
		return this.port;
	}
}
