package org.greatfree.message;

/*
 * The message is sent to the remote server to ensure a relevant instance of ObjectOutputStream is initialized on the server. If so, it means that the local ObjectInputStream can be initialized. Otherwise, the client must get stuck. To avoid the problem, the message is required to be sent if the local client needs to receive data from the server. 11/03/2014, Bing Li
 */

// Created: 11/03/2014, Bing Li
public class InitReadNotification extends ServerMessage
{
	private static final long serialVersionUID = -559357101111103183L;

	// It is required to send the client key to the remote server such that the server is able to retrieve the client socket on the server to notify the local client to initialize ObjectInputStream. 11/03/2014, Bing Li
	private String clientKey;

	/*
	 * Initialize. 11/03/2014, Bing Li
	 */
	public InitReadNotification(String clientKey)
	{
		super(SystemMessageType.INIT_READ_NOTIFICATION);
		this.clientKey = clientKey;
	}

	/*
	 * Expose the client key. 11/03/2014, Bing Li
	 */
	public String getClientKey()
	{
		return this.clientKey;
	}
}
