package org.greatfree.cache.message.child;

import java.util.HashMap;

import org.greatfree.cache.message.CloseNotification;
import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates the notifications of CloseNotification that should be broadcast to the current distributed node's children. 07/14/2017, Bing Li
 */

// Created: 07/14/2017, Bing L
public class CloseNotificationCreator implements ChildBroadcastNotificationCreatable<CloseNotification>
{

	@Override
	public CloseNotification createInstanceWithChildren(HashMap<String, IPAddress> children, CloseNotification msg)
	{
//		return new CloseNotification(Tools.generateUniqueKey(), children);
		return new CloseNotification(children);
	}

}
