package org.greatfree.cache.message.child;

import java.util.HashMap;

import org.greatfree.cache.message.BroadKeysRequest;
import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates request of BroadKeysRequest that should be broadcast to the current distributed node's children. 07/14/2017, Bing Li
 */

// Created: 07/14/2017, Bing Li
public class BroadKeysRequestCreator implements ChildBroadcastRequestCreatable<BroadKeysRequest>
{

	@Override
	public BroadKeysRequest createInstanceWithChildren(HashMap<String, IPAddress> children, BroadKeysRequest msg)
	{
//		return new BroadKeysRequest(Tools.generateUniqueKey(), msg.getCollaboratorKey(), children);
		return new BroadKeysRequest(children);
	}

}
