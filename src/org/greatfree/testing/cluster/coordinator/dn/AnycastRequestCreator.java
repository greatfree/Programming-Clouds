package org.greatfree.testing.cluster.coordinator.dn;

import java.util.HashMap;

import org.greatfree.multicast.RootBroadcastRequestCreatable;
import org.greatfree.testing.message.DNAnycastRequest;
import org.greatfree.util.Tools;

/*
 * This creator aims to create requests of AnycastRequest, in the root broadcast requester. 11/29/2014, Bing Li
 */

// Created: 11/27/2016, Bing Li
public class AnycastRequestCreator implements RootBroadcastRequestCreatable<DNAnycastRequest, String>
{
	/*
	 * Create the anycast request for the node which has children. 11/29/2014, Bing Li
	 */
	@Override
	public DNAnycastRequest createInstanceWithChildren(String t, String collaboratorKey, HashMap<String, String> childrenMap)
	{
		return new DNAnycastRequest(t, Tools.generateUniqueKey(), collaboratorKey, childrenMap);
	}

	/*
	 * Create the anycast request for the node which has no children. 11/29/2014, Bing Li
	 */
	@Override
	public DNAnycastRequest createInstanceWithoutChildren(String t, String collaboratorKey)
	{
		return new DNAnycastRequest(t, Tools.generateUniqueKey(), collaboratorKey);
	}

}
