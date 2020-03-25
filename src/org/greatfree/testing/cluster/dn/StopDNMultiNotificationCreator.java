package org.greatfree.testing.cluster.dn;

import java.util.HashMap;

import org.greatfree.multicast.ChildMulticastMessageCreatable;
import org.greatfree.testing.message.StopDNMultiNotification;
import org.greatfree.util.Tools;

/*
 * The creator generates the notifications of StopDNMultiNotification that should be multicast to the local DN's children. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNMultiNotificationCreator implements ChildMulticastMessageCreatable<StopDNMultiNotification>
{

	@Override
	public StopDNMultiNotification createInstanceWithChildren(StopDNMultiNotification msg, HashMap<String, String> children)
	{
		return new StopDNMultiNotification(Tools.generateUniqueKey(), children);
	}

}
