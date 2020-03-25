package org.greatfree.message.multicast;

import java.util.List;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

/*
 * The request is sent to the Registry Server of the peer based system to retrieve all of registered IP addresses of the cluster. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class ClusterIPRequest extends ServerMessage
{
	private static final long serialVersionUID = 49780739482646513L;
	
	private List<String> nodes;

	public ClusterIPRequest()
	{
		super(SystemMessageType.CLUSTER_IP_REQUEST);
		this.nodes = null;
	}

	public ClusterIPRequest(List<String> nodes)
	{
		super(SystemMessageType.CLUSTER_IP_REQUEST);
		this.nodes = nodes;
	}
	
	public List<String> getNodes()
	{
		return this.nodes;
	}
}
