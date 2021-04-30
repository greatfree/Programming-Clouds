package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

/*
 * The request is sent from the meta server to the cluster of storage servers to inquiry the storage states. It is performed when the last updated time exceeds a predefind period. 07/13/2020, Bing Li
 */

// Created: 07/13/2020, Bing Li
public class SSStateRequest extends Request
{
	private static final long serialVersionUID = -1678549175123319850L;

	public SSStateRequest()
	{
		super(MulticastMessageType.BROADCAST_REQUEST, S3AppID.SSSTATE_REQUEST);
	}
}
