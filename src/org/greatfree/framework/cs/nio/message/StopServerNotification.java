package org.greatfree.framework.cs.nio.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/08/2022
 *
 */
public class StopServerNotification extends ServerMessage
{
	private static final long serialVersionUID = 2836168168579474025L;
	
	public StopServerNotification()
	{
		super(NIOMsgType.STOP_SERVER_NOTIFICATION);
	}

}
