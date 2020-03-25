package org.greatfree.dip.old.multicast.child;

import java.util.HashMap;

import org.greatfree.dip.multicast.message.OldHelloWorldBroadcastNotification;
import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates the notifications of HelloWorldNotification that should be broadcast to the current distributed node's children. 05/12/2017, Bing Li
 */

// Created: 05/12/2017, Bing Li
class HelloWorldBroadcastNotificationCreator implements ChildBroadcastNotificationCreatable<OldHelloWorldBroadcastNotification>
{

	@Override
	public OldHelloWorldBroadcastNotification createInstanceWithChildren(HashMap<String, IPAddress> children, OldHelloWorldBroadcastNotification msg)
	{
//		return new HelloWorldBroadcastNotification(Tools.generateUniqueKey(), children, msg.getHelloWorld());
		return new OldHelloWorldBroadcastNotification(children, msg.getHelloWorld());
	}

}
