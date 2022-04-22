package org.greatfree.framework.cs.nio.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
public class MyNotification extends ServerMessage
{
	private static final long serialVersionUID = -2420901765041796461L;
	
	private String message;

	public MyNotification(String message)
	{
		super(NIOMsgType.MY_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
