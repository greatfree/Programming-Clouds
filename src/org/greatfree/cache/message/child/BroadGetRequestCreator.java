package org.greatfree.cache.message.child;

import java.util.HashMap;

import org.greatfree.cache.message.BroadGetRequest;
import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates request of BroadGetRequest that should be broadcast to the current distributed node's children. 07/14/2017, Bing Li
 */

// Created: 07/14/2017, Bing Li
public class BroadGetRequestCreator implements ChildBroadcastRequestCreatable<BroadGetRequest>
{

	@Override
	public BroadGetRequest createInstanceWithChildren(HashMap<String, IPAddress> children, BroadGetRequest msg)
	{
//		return new BroadGetRequest(Tools.generateUniqueKey(), msg.getCollaboratorKey(), children, msg.getRequestedKeys());
		return new BroadGetRequest(children, msg.getRequestedKeys());
	}

}
