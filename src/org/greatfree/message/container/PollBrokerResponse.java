package org.greatfree.message.container;

import java.util.List;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 03/09/2023
 *
 */
public class PollBrokerResponse extends MulticastResponse
{
	private static final long serialVersionUID = -8617948502056695664L;
	
	private List<Response> responses;

	public PollBrokerResponse(List<Response> responses, String collaboratorKey)
	{
		super(SystemMessageType.POLL_BROKER_RESPONSE, collaboratorKey);
		this.responses = responses;
	}

	public List<Response> getResponses()
	{
		return this.responses;
	}
}
