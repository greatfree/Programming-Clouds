package edu.greatfree.cs.multinode.message;

import org.greatfree.message.ServerMessage;

//Created: 04/17/2017, Bing Li
public class ShutdownServerNotification extends ServerMessage
{
	private static final long serialVersionUID = -3708037901134852203L;

	public ShutdownServerNotification()
	{
		super(ChatMessageType.SHUTDOWN_SERVER_NOTIFICATION);
		// TODO Auto-generated constructor stub
	}

}
