package org.greatfree.multicast;

import java.io.Serializable;
import java.util.HashMap;

import org.greatfree.message.abandoned.BroadcastRequest;

/*
 * The interface defines the methods to create request in the broadcast requestor. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public interface RootBroadcastRequestCreatable<Request extends BroadcastRequest, ObjectedData extends Serializable>
{
	// Create the request for the requestor that have children nodes. 11/29/2014, Bing Li
	public Request createInstanceWithChildren(ObjectedData t, String collaboratorKey, HashMap<String, String> childrenMap);
	// Create the request for the requestor that have no children nodes. 11/29/2014, Bing Li
	public Request createInstanceWithoutChildren(ObjectedData t, String collaboratorKey);
}
