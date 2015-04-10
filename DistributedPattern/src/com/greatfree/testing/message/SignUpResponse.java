package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * The class is an example of the response, which must derive from the base class, ServerMessage. 09/21/2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class SignUpResponse extends ServerMessage
{
	private static final long serialVersionUID = -3347732482627702119L;

	// The data that must be transmitted from the polling server to the client. 09/21/2014, Bing Li
	private boolean isSucceeded;

	/*
	 * Initialize. 09/21/2014, Bing Li
	 */
	public SignUpResponse(boolean isSucceeded)
	{
		super(MessageType.SIGN_UP_RESPONSE);
		this.isSucceeded = isSucceeded;
	}

	/*
	 * Expose the responded data. 09/21/2014, Bing Li
	 */
	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
