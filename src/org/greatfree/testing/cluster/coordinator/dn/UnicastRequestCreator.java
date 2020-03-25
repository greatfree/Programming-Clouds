package org.greatfree.testing.cluster.coordinator.dn;

import java.util.HashMap;

import org.greatfree.multicast.RootBroadcastRequestCreatable;
import org.greatfree.testing.message.UnicastRequest;
import org.greatfree.util.Tools;

/*
 * This creator aims to create requests of UnicastRequest, in the root broadcast requester. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastRequestCreator implements RootBroadcastRequestCreatable<UnicastRequest, String>
{
	/*
	 * Create the unicast request for the node which has children. 11/29/2014, Bing Li
	 */
	@Override
	public UnicastRequest createInstanceWithChildren(String t, String collaboratorKey, HashMap<String, String> childrenMap)
	{
		return new UnicastRequest(t, Tools.generateUniqueKey(), collaboratorKey, childrenMap);
	}

	/*
	 * Create the unicast request for the node which has no children. 11/29/2014, Bing Li
	 */
	@Override
	public UnicastRequest createInstanceWithoutChildren(String t, String collaboratorKey)
	{
		return new UnicastRequest(t, Tools.generateUniqueKey(), collaboratorKey);
	}

}
