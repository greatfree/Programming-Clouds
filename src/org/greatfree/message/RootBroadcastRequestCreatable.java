package org.greatfree.message;

import java.io.Serializable;
import java.util.HashMap;

import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.util.IPAddress;

/*
 * The interface defines the methods to create request in the broadcast requestor. 11/28/2014, Bing Li
 */

// Created: 05/13/2017, Bing Li
// public interface RootBroadcastRequestCreatable<Request extends MulticastRequest, ObjectedData extends Serializable>
public interface RootBroadcastRequestCreatable<Request extends OldMulticastRequest, ObjectedData extends Serializable>
{
	// Create the request for the requestor that have children nodes. 11/29/2014, Bing Li
//	public Request createInstanceWithChildren(ObjectedData t, String collaboratorKey, IPAddress rootAddress, HashMap<String, IPAddress> childrenMap);
//	public Request createInstanceWithChildren(String collaboratorKey, HashMap<String, IPAddress> childrenMap, ObjectedData t);
	public Request createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, ObjectedData t);
	// Create the request for the requestor that have no children nodes. 11/29/2014, Bing Li
//	public Request createInstanceWithoutChildren(ObjectedData t, String collaboratorKey, IPAddress rootAddress);
//	public Request createInstanceWithoutChildren(String collaboratorKey, ObjectedData t);
	public Request createInstanceWithoutChildren(ObjectedData t);
}
