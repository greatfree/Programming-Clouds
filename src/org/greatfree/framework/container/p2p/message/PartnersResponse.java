package org.greatfree.framework.container.p2p.message;

import java.util.Map;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPPort;

// Created: 09/13/2019, Bing Li
public class PartnersResponse extends ServerMessage
{
	private static final long serialVersionUID = -8815541372011026565L;
	
	private Map<String, IPPort> ips;

	public PartnersResponse(Map<String, IPPort> ips)
	{
		super(P2PChatApplicationID.PARTNERS_RESPONSE);
		this.ips = ips;
	}

	public Map<String, IPPort> getIPs()
	{
		return this.ips;
	}
}
