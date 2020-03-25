package org.greatfree.message.multicast;

import java.util.Map;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.util.IPAddress;

/*
 * All of registered IP addresses are enclosed in the message. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class ClusterIPResponse extends ServerMessage
{
	private static final long serialVersionUID = -7503167324503868071L;
	
	private Map<String, IPAddress> ips;

	public ClusterIPResponse(Map<String, IPAddress> ips)
	{
		super(SystemMessageType.CLUSTER_IP_RESPONSE);
		this.ips = ips;
	}
	
	public ClusterIPResponse()
	{
		super(SystemMessageType.CLUSTER_IP_RESPONSE);
		this.ips = null;
	}

	public Map<String, IPAddress> getIPs()
	{
		return this.ips;
	}
}
