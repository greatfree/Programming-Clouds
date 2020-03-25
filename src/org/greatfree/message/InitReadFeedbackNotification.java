package org.greatfree.message;

/*
 * 
 * The message is sent as a notification by a server after its corresponding ObjectOutputStream is initialized. It notifies the client that the server is ready for output. Then, the client is allowed to initialize ObjectInputStream and receive data from the server. 11/07/2014, Bing Li
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
		super(SystemMessageType.INIT_READ_FEEDBACK_NOTIFICATION);
	}
}
