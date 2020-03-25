package org.greatfree.message.abandoned;

import java.util.HashMap;

import org.greatfree.message.ServerMulticastMessage;

/*
 * The message is a broadcast request to be sent through all of the distributed nodes to retrieve required data. For multicasting is required, it extends ServerMulticastMessage. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class BroadcastRequest extends ServerMulticastMessage
{
	private static final long serialVersionUID = -5477115431261715561L;

	// To implement the broadcast, the request contains the collaborator key that is associated with the instance of Collaborator that is waiting at the source node that sends the broadcast request. Once if the required data is obtained at one node, it must be returned to the source from the destination. The collaborator must be signaled by the responses. As a broadcast, all of the nodes must respond before the requesting is finished. 11/28/2014, Bing Li
	private String collaboratorKey;

	/*
	 * Initialize. The parameters are explained as follows. 11/28/2014, Bing Li
	 * 
	 * 		int dataType: the message type;
	 * 
	 * 		String key: the message key;
	 * 
	 * 		String collaboratorKey: the collaborator key pointing to the instance of Collaborator to collect all of the results by synchronization
	 * 
	 * 		HashMap<String, String> childrenServerMap: when an intermediate node receives the request, it must forward the request to its children with the assistance of information.	
	 */
	public BroadcastRequest(int dataType, String key, String collaboratorKey, HashMap<String, String> childrenServerMap)
	{
		super(dataType, key, childrenServerMap);
		this.collaboratorKey = collaboratorKey;
	}

	/*
	 * Initialize. This constructor is used for the case when the requestor has no children nodes. 11/28/2014, Bing Li
	 */
	public BroadcastRequest(int dataType, String key, String collaboratorKey)
	{
		super(dataType, key);
		this.collaboratorKey = collaboratorKey;
	}

	/*
	 * Expose the collaborator. 11/28/2014, Bing Li
	 */
	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
