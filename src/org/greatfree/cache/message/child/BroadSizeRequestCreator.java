package org.greatfree.cache.message.child;

import java.util.HashMap;

import org.greatfree.cache.message.BroadSizeRequest;
import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates request of BroadSizeRequest that should be broadcast to the current distributed node's children. 07/14/2017, Bing Li
 */

// Created: 07/14/2017, Bing Li
public class BroadSizeRequestCreator implements ChildBroadcastRequestCreatable<BroadSizeRequest>
{

	@Override
	public BroadSizeRequest createInstanceWithChildren(HashMap<String, IPAddress> children, BroadSizeRequest msg)
	{
//		return new BroadSizeRequest(Tools.generateUniqueKey(), msg.getCollaboratorKey(), children);
		return new BroadSizeRequest(children);
	}

}
