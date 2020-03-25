package org.greatfree.testing.cluster.dn;

import java.util.HashMap;

import org.greatfree.multicast.ChildMulticastMessageCreatable;
import org.greatfree.testing.message.DNBroadcastRequest;
import org.greatfree.util.Tools;

/*
 * The creator initiates the instance of DNBroadcastRequest that is needed by the multicastor. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class DNBroadcastRequestCreator implements ChildMulticastMessageCreatable<DNBroadcastRequest>
{

	@Override
	public DNBroadcastRequest createInstanceWithChildren(DNBroadcastRequest msg, HashMap<String, String> children)
	{
		return new DNBroadcastRequest(msg.getRequest(), Tools.generateUniqueKey(), msg.getCollaboratorKey(), children);
	}

}
