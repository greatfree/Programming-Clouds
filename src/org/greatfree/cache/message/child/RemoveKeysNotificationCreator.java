package org.greatfree.cache.message.child;

import java.util.HashMap;

import org.greatfree.cache.message.RemoveKeysNotification;
import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates the notifications of RemoveKeysNotification that should be broadcast to the current distributed node's children. 07/14/2017, Bing Li
 */

// Created: 07/14/2017, Bing Li
public class RemoveKeysNotificationCreator implements ChildBroadcastNotificationCreatable<RemoveKeysNotification>
{

	@Override
	public RemoveKeysNotification createInstanceWithChildren(HashMap<String, IPAddress> children, RemoveKeysNotification msg)
	{
//		return new RemoveKeysNotification(Tools.generateUniqueKey(), children, msg.getKeys());
		return new RemoveKeysNotification(children, msg.getKeys());
	}

}
