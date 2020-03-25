package org.greatfree.testing.cluster.dn;

import org.greatfree.reuse.HashCreatable;

/*
 * The creator initializes an instance of DNBroadcastRequestChildBroadcastor. It works with the resource pool to utilize the broadcastor efficiently. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class DNBroadcastRequestChildBroadcastorCreator implements HashCreatable<DNBroadcastRequestChildBroadcastorSource, DNBroadcastRequestChildBroadcastor>
{

	@Override
	public DNBroadcastRequestChildBroadcastor createResourceInstance(DNBroadcastRequestChildBroadcastorSource source)
	{
		return new DNBroadcastRequestChildBroadcastor(source.getClientPool(), source.getTreeBranchCount(), source.getServerPort(), source.getMessageCreator());
	}

}
