package org.greatfree.dsf.old.multicast.child;

import java.util.HashMap;

import org.greatfree.dsf.multicast.message.OldHelloWorldBroadcastRequest;
import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates the request of HelloWorldBroadcastRequest that should be broadcast to the current distributed node's children. 05/20/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
class HelloWorldBroadcastRequestCreator implements ChildBroadcastRequestCreatable<OldHelloWorldBroadcastRequest>
{

	@Override
	public OldHelloWorldBroadcastRequest createInstanceWithChildren(HashMap<String, IPAddress> children, OldHelloWorldBroadcastRequest msg)
	{
//		return new HelloWorldBroadcastRequest(Tools.generateUniqueKey(), msg.getCollaboratorKey(), children, msg.getHelloWorld());
		return new OldHelloWorldBroadcastRequest(children, msg.getHelloWorld());
	}

}
