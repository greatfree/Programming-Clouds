package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 03/06/2023
 *
 */
public class SearchBrokerRequest extends Request
{
	private static final long serialVersionUID = -7753766585205945486L;
	
//	private IPAddress ip;

//	public BrokerRequest(IPAddress ip)
	public SearchBrokerRequest()
	{
		super(SystemMessageType.SEARCH_BROKER_REQUEST);
//		this.ip = ip;
	}

	/*
	public IPAddress getIP()
	{
		return this.ip;
	}
	*/
}
