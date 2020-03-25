package org.greatfree.testing.memory;

import java.util.HashMap;

import org.greatfree.multicast.ChildMulticastMessageCreatable;
import org.greatfree.testing.message.IsPublisherExistedAnycastRequest;
import org.greatfree.util.Tools;

/*
 * The creator initializes the multicast requests in the child multicastor to accomplish the goal the anycast requesting. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedRequestCreator implements ChildMulticastMessageCreatable<IsPublisherExistedAnycastRequest>
{
	// Initialize an instance of the anycast request. 11/29/2014, Bing Li
	@Override
	public IsPublisherExistedAnycastRequest createInstanceWithChildren(IsPublisherExistedAnycastRequest msg, HashMap<String, String> children)
	{
		return new IsPublisherExistedAnycastRequest(msg.getURL(), Tools.generateUniqueKey(), msg.getCollaboratorKey(), children);
	}
}
