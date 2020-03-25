package org.greatfree.message.abandoned;

import org.greatfree.message.ServerMessage;

/*
 * The message is a broadcast response to be responded to the initial requester after retrieving the required data. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class BroadcastResponse extends ServerMessage
{
	private static final long serialVersionUID = 200920679139533863L;

	// The message key. 11/28/2014, Bing Li
	private String key;
	// The collaborator to assist the synchronization for collecting broadcast retrieved data. 11/28/2014, Bing Li
	private String collaboratorKey;

	/*
	 * Initialize. The parameters are explained as follows. 11/28/2014, Bing Li
	 * 
	 * 		int dataType: the message type;
	 * 
	 * 		String key: the message key;
	 * 
	 * 		String collaboratorKey: the collaborator key pointing to the instance of Collaborator to collect all of the results by synchronization
	 */
	public BroadcastResponse(int dataType, String key, String collaboratorKey)
	{
		super(dataType);
//		super(dataType, key);
		this.key = key;
		this.collaboratorKey = collaboratorKey;
	}

	/*
	 * Expose the key. 11/28/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * Expose the collaborator key. 11/28/2014, Bing Li
	 */
	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
