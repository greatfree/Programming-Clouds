package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * 
 * The message is sent as a notification by a server after its corresponding ObjectOutputStream is initialized. It notifies the cl
 */

// Created: 11/07/2014, Bing Li
public class InitReadFeedbackNotification extends ServerMessage
{
	private static final long serialVersionUID = 4474239685077563637L;

	/*
	 * Initialize. It is just a notification without additional information. 11/07/2014, Bing Li
	 */
	public InitReadFeedbackNotification()
	{
		super(MessageType.INIT_READ_FEEDBACK_NOTIFICATION);
	}
}
