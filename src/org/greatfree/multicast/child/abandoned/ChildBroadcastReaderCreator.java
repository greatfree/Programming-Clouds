package org.greatfree.multicast.child.abandoned;

import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.reuse.HashCreatable;

// Created: 05/07/2017, Bing Li
class ChildBroadcastReaderCreator<Request extends OldMulticastRequest, RequestCreator extends ChildBroadcastRequestCreatable<Request>> implements HashCreatable<ChildBroadcastReaderSource<Request, RequestCreator>, ChildBroadcastReader<Request, RequestCreator>>
{

	@Override
	public ChildBroadcastReader<Request, RequestCreator> createResourceInstance(ChildBroadcastReaderSource<Request, RequestCreator> source)
	{
		return new ChildBroadcastReader<Request, RequestCreator>(source.getLocalIPKey(), source.getClientPool(), source.getTreeBranchCount(), source.getMessageCreator());
	}

}
