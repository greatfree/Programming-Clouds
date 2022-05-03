package org.greatfree.message.multicast;

import org.greatfree.message.ServerMessage;

/*
 * The message is a broadcast response to be responded to the initial requester after retrieving the required data. 11/28/2014, Bing Li
 */

// Created: 05/13/2017, Bing Li
public class MulticastResponse extends ServerMessage
{
	private static final long serialVersionUID = 6563131960227489984L;

	// The message key. 11/28/2014, Bing Li
//	private String key;
	// The collaborator to assist the synchronization for collecting broadcast retrieved data. 11/28/2014, Bing Li
	private String collaboratorKey;
	private int applicationID;

	/*
	 * Initialize. The parameters are explained as follows. 11/28/2014, Bing Li
	 * 
	 * 		int dataType: the message type;
	 * 
	 * 		String key: the message key;
	 * 
	 * 		String collaboratorKey: the collaborator key pointing to the instance of Collaborator to collect all of the results by synchronization
	 */
//	public MulticastResponse(int type, String key, String collaboratorKey)
//	public MulticastResponse(int type, String collaboratorKey)
	public MulticastResponse(int applicationID, String collaboratorKey)
	{
//		super(type);
		super(MulticastMessageType.MULTICAST_RESPONSE);
//		this.key = key;
		this.applicationID = applicationID;
		this.collaboratorKey = collaboratorKey;
	}
	
	public int getApplicationID()
	{
		return this.applicationID;
	}

	/*
	 * Expose the key. 11/28/2014, Bing Li
	 */
	/*
	public String getKey()
	{
		return this.key;
	}
	*/

	/*
	 * Expose the collaborator key. 11/28/2014, Bing Li
	 */
	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
