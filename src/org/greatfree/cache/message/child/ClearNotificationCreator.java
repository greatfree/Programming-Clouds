package org.greatfree.cache.message.child;

import java.util.HashMap;

import org.greatfree.cache.message.ClearNotification;
import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates the notifications of ClearNotification that should be broadcast to the current distributed node's children. 07/14/2017, Bing Li
 */

// Created: 07/14/2017, Bing Li
public class ClearNotificationCreator implements ChildBroadcastNotificationCreatable<ClearNotification>
{

	@Override
	public ClearNotification createInstanceWithChildren(HashMap<String, IPAddress> children, ClearNotification msg)
	{
//		return new ClearNotification(Tools.generateUniqueKey(), children);
		return new ClearNotification(children);
	}

}
