package org.greatfree.framework.cs.nio.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
public class MyResponse extends ServerMessage
{
	private static final long serialVersionUID = 1415961000504157648L;
	
	private String message;

	public MyResponse(String message)
	{
		super(NIOMsgType.MY_RESPONSE);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
