package org.greatfree.framework.container.p2p.message;

import java.util.Map;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

// Created: 09/21/2019, Bing Li
public class AllRegisteredIPsResponse extends ServerMessage
{
	private static final long serialVersionUID = -7340548471239691665L;

	private Map<String, IPAddress> ips;

	public AllRegisteredIPsResponse(Map<String, IPAddress> ips)
	{
		super(P2PChatApplicationID.ALL_REGISTERED_IPS_RESPONSE);
		this.ips = ips;
	}

	public Map<String, IPAddress> getIPs()
	{
		return this.ips;
	}
}
