package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastResponse;

import edu.chainnet.s3.meta.table.SSState;

// Created: 07/13/2020, Bing Li
public class SSStateResponse extends MulticastResponse
{
	private static final long serialVersionUID = 1139796503250665795L;
	
	private SSState state;

	public SSStateResponse(SSState state, String collaboratorKey)
	{
		super(S3AppID.SSSTATE_RESPONSE, collaboratorKey);
		this.state = state;
	}

	public SSState getState()
	{
		return this.state;
	}
}
