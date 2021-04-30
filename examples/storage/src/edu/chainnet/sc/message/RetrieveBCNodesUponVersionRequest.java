package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

/*
 * The message retrieves historical BC nodes upon versions. 10/19/2020, Bing Li
 */

// Created: 10/19/2020, Bing Li
public class RetrieveBCNodesUponVersionRequest extends Request
{
	private static final long serialVersionUID = 3042291771484029191L;
	
	private int version;

	public RetrieveBCNodesUponVersionRequest(int version)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, SCAppID.RETRIEVE_BC_NODES_UPON_VERSION_REQUEST);
		this.version = version;
	}

	public int getVersion()
	{
		return this.version;
	}
}
