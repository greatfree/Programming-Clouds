package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.container.Request;

// Created: 01/12/2019, Bing Li
public class ClusterIPRequest extends Request
{
	private static final long serialVersionUID = -4144355741551553296L;
	
	private String rootKey;

	public ClusterIPRequest(String rootKey)
	{
		super(P2PChatApplicationID.CLUSTER_IP_REQUEST);
		this.rootKey = rootKey;
	}

	public String getRootKey()
	{
		return this.rootKey;
	}
}
