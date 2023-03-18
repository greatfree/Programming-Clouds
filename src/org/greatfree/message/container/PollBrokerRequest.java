package org.greatfree.message.container;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.multicast.container.ClusterRequest;

/**
 * 
 * @author libing
 * 
 * 03/09/2023
 *
 */
public class PollBrokerRequest extends ClusterRequest
{
	private static final long serialVersionUID = -4190280666802246120L;
	
	private String peerName;

	public PollBrokerRequest(String peerName)
	{
		super(peerName, SystemMessageType.POLL_BROKER_REQUEST);
	}

	public String getPeerName()
	{
		return this.peerName;
	}
}
