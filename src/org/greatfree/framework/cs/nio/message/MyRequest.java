package org.greatfree.framework.cs.nio.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
public class MyRequest extends ServerMessage
{
	private static final long serialVersionUID = 8042255583679529879L;
	
	private String message;

	public MyRequest(String message)
	{
		super(NIOMsgType.MY_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
