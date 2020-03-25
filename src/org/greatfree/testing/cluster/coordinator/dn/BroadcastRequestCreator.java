package org.greatfree.testing.cluster.coordinator.dn;

import java.util.HashMap;

import org.greatfree.multicast.RootBroadcastRequestCreatable;
import org.greatfree.testing.message.DNBroadcastRequest;
import org.greatfree.util.Tools;

/*
 * This creator aims to create requests of DNBroadcastRequest, in the root broadcast requester. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class BroadcastRequestCreator implements RootBroadcastRequestCreatable<DNBroadcastRequest, String>
{
	/*
	 * Create the broadcast request for the node which has children. 11/29/2014, Bing Li
	 */
	@Override
	public DNBroadcastRequest createInstanceWithChildren(String t, String collaboratorKey, HashMap<String, String> childrenMap)
	{
		return new DNBroadcastRequest(t, Tools.generateUniqueKey(), collaboratorKey, childrenMap);
	}

	/*
	 * Create the broadcast request for the node which has no children. 11/29/2014, Bing Li
	 */
	@Override
	public DNBroadcastRequest createInstanceWithoutChildren(String t, String collaboratorKey)
	{
		return new DNBroadcastRequest(t, Tools.generateUniqueKey(), collaboratorKey);
	}

}
