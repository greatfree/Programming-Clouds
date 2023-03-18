package org.greatfree.framework.cs.disabled.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 03/07/2023
 *
 */
public class DSResponse extends ServerMessage
{
	private static final long serialVersionUID = -5261988710944577563L;
	
	private String message;

	public DSResponse(String message)
	{
		super(DisabledAppID.DS_RESPONSE);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
