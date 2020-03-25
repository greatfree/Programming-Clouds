package org.greatfree.message;

import java.util.HashMap;

import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.util.IPAddress;

/*
 * The interface defines the method to create a multicast request on a child node rather than the root one. 11/10/2014, Bing Li
 */

// Created: 05/13/2017, Bing Li
public interface ChildBroadcastRequestCreatable<Request extends OldMulticastRequest>
{
	public Request createInstanceWithChildren(HashMap<String, IPAddress> children, Request msg);
}
