package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

/*
 * The message retrieves historical DS nodes upon versions. 10/19/2020, Bing Li
 */

// Created: 10/19/2020, Bing Li
public class RetrieveDSNodesUponVersionRequest extends Request
{
	private static final long serialVersionUID = 2994361655527533317L;
	
	private int version;
	private int nodeType;

	public RetrieveDSNodesUponVersionRequest(int version, int nodeType)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, SCAppID.RETRIEVE_DS_NODES_UPON_VERSION_REQUEST);
		this.version = version;
		this.nodeType = nodeType;
	}

	public int getVersion()
	{
		return this.version;
	}
	
	public int getNodeType()
	{
		return this.nodeType;
	}
}
