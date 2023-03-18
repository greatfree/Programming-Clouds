package org.greatfree.framework.cs.disabled.broker.message;

import org.greatfree.message.multicast.container.ClusterRequest;

/**
 * 
 * @author libing
 * 
 * 03/17/2023
 *
 */
public class PollRequest extends ClusterRequest
{
	private static final long serialVersionUID = 5747015509342347734L;
	
	private String destinationName;

	public PollRequest(String destinationName)
	{
		super(destinationName, DisabledAppID.POLL_REQUEST);
		this.destinationName = destinationName;
	}

	public String getDestinationName()
	{
		return this.destinationName;
	}
}
