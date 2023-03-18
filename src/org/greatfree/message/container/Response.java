package org.greatfree.message.container;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

/**
 * 
 * The message is used particularly for its corresponding one, Request. It is accessible from the Broker cluster. 03/09/2023, Bing Li
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
// public abstract class Response extends ServerMessage
public class Response extends ServerMessage
{
	private static final long serialVersionUID = -2423298448517643591L;
	
	private ServerMessage message;

	public Response(ServerMessage message, String requestKey)
	{
		super(SystemMessageType.RESPONSE, requestKey);
		this.message = message;
	}
	
	public ServerMessage getMessage()
	{
		return this.message;
	}
}
