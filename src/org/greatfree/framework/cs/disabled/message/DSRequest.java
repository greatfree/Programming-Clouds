package org.greatfree.framework.cs.disabled.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 03/07/2023
 *
 */
public class DSRequest extends Request
{
	private static final long serialVersionUID = 3959100007349454827L;
	
	private String message;

	public DSRequest(String message)
	{
		super(DisabledAppID.DS_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
