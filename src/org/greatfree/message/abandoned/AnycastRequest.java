package org.greatfree.message.abandoned;

import java.util.HashMap;

import org.greatfree.message.ServerMulticastMessage;

/*
 * The request is a multicast one that is sent to all of the nodes in a cluster. However, once if one node at least responds the request positively, the multicast requesting must be terminated. That is the difference from the broadcast requesting. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class AnycastRequest extends ServerMulticastMessage
{
	private static final long serialVersionUID = 1555137356272335216L;

	// To implement the anycast, the request contains the collaborator key that is associated with the instance of Collaborator that is waiting at the source node that sends the anycast request. Once if the required data is obtained at one node, it must be returned to the source from the destination. The collaborator must be signaled by the responses. As an anycast, only one response from all of the nodes can terminate the requesting. 11/28/2014, Bing Li
	private String collaboratorKey;

	/*
	 * Initialize. The parameters are explained as follows. 11/29/2014, Bing Li
	 * 
	 * 		int dataType: the message type;
	 * 
	 * 		String key: the message key;
	 * 
	 * 		String collaboratorKey: the collaborator key pointing to the instance of Collaborator to collect all of the results by synchronization
	 * 
	 * 		HashMap<String, String> childrenServerMap: when an intermediate node receives the request, it must forward the request to its children with the assistance of information.	
	 */
	public AnycastRequest(int messageType, String key, String collaboratorKey, HashMap<String, String> childrenServerMap)
	{
		super(messageType, key);
		this.collaboratorKey = collaboratorKey;
	}

	/*
	 * Initialize. This constructor is used for the case when the requestor has no children nodes. 11/28/2014, Bing Li
	 */
	public AnycastRequest(int messageType, String key, String collaboratorKey)
	{
		super(messageType, key);
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
