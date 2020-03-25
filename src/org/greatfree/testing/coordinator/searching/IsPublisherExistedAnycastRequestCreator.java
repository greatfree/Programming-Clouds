package org.greatfree.testing.coordinator.searching;

import java.util.HashMap;

import org.greatfree.multicast.RootAnycastRequestCreatable;
import org.greatfree.testing.message.IsPublisherExistedAnycastRequest;
import org.greatfree.util.Tools;

/*
 * This creator aims to create requests of IsPublisherExistedAnycastRequest, in the root anycast requester. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedAnycastRequestCreator implements RootAnycastRequestCreatable<IsPublisherExistedAnycastRequest, String>
{
	/*
	 * Create the anycast request for the node which has children. 11/29/2014, Bing Li
	 */
	@Override
	public IsPublisherExistedAnycastRequest createInstanceWithChildren(String t, String collaboratorKey, HashMap<String, String> childrenMap)
	{
		return new IsPublisherExistedAnycastRequest(Tools.generateUniqueKey(), t, collaboratorKey, childrenMap);
	}

	/*
	 * Create the anycast request for the node which has no children. 11/29/2014, Bing Li
	 */
	@Override
	public IsPublisherExistedAnycastRequest createInstanceWithoutChildren(String t, String collaboratorKey)
	{
		return new IsPublisherExistedAnycastRequest(Tools.generateUniqueKey(), t, collaboratorKey);
	}
}
