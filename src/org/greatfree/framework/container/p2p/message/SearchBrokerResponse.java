package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.util.IPAddress;

/**
 * 
 * @author libing
 * 
 * 03/06/2023
 *
 */
public class SearchBrokerResponse extends ServerMessage
{
	private static final long serialVersionUID = -7613471622182026048L;
	
	private IPAddress broker;

	public SearchBrokerResponse(IPAddress broker)
	{
		super(SystemMessageType.SEARCH_BROKER_RESPONSE);
		this.broker = broker;
	}

	public IPAddress getBroker()
	{
		return this.broker;
	}
}
