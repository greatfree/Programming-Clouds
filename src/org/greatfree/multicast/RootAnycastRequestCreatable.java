package org.greatfree.multicast;

import java.io.Serializable;
import java.util.HashMap;

import org.greatfree.message.abandoned.AnycastRequest;

/*
 * The interface defines the methods to create requests in the anycastor. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface RootAnycastRequestCreatable<Request extends AnycastRequest, MessagedData extends Serializable>
{
	// Create the request for the requestor that have children nodes. 11/29/2014, Bing Li
	public Request createInstanceWithChildren(MessagedData t, String collaboratorKey, HashMap<String, String> childrenMap);
	// Create the request for the requestor that have no children nodes. 11/29/2014, Bing Li
	public Request createInstanceWithoutChildren(MessagedData t, String collaboratorKey);
}
