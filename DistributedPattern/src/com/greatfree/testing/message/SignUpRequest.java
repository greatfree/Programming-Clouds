package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * The class is an example of a request for signing up. It must derive from the base class, ServerMessage. 09/21/2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class SignUpRequest extends ServerMessage
{
	private static final long serialVersionUID = -4781998022159903485L;

	// Data in the request to be sent to the polling server. 09/21/2014, Bing Li
	private String userName;
	// Data in the request to be sent to the polling server. 09/21/2014, Bing Li
	private String password;

	/*
	 * Initialize. 09/21/2014, Bing Li
	 */
	public SignUpRequest(String userName, String password)
	{
		super(MessageType.SIGN_UP_REQUEST);
		this.userName = userName;
		this.password = password;
	}

	/*
	 * Expose the userName. 09/21/2014, Bing Li
	 */
	public String getUserName()
	{
		return this.userName;
	}

	/*
	 * Expose the password. 09/21/2014, Bing Li
	 */
	public String getPassword()
	{
		return this.password;
	}
}
