package org.greatfree.message.abandoned;

import org.greatfree.message.ServerMessage;

/*
 * The message is an anycast response to be responded to the initial requester after retrieving the required data. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class AnycastResponse extends ServerMessage
{
	private static final long serialVersionUID = 1170590433477187242L;

	// The collaborator to assist the synchronization for collecting broadcast retrieved data. 11/29/2014, Bing Li
	private String collaboratorKey;

	/*
	 * Initialize. The parameters are explained as follows. 11/29/2014, Bing Li
	 * 
	 * 		int dataType: the message type;
	 * 
	 * 		String collaboratorKey: the collaborator key pointing to the instance of Collaborator to collect all of the results by synchronization
	 */
	public AnycastResponse(int type, String collaboratorKey)
	{
		super(type);
		this.collaboratorKey = collaboratorKey;
	}

	/*
	 * Expose the collaborator key. 11/29/2014, Bing Li
	 */
	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
