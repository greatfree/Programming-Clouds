package org.greatfree.multicast.child.abandoned;

import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the resource of a broadcastor. It is used by the resource pool to save resources. 05/07/2017, Bing Li
 */

// Created: 05/07/2017, Bing Li
class ChildBroadcastReaderDisposer<Request extends OldMulticastRequest, RequestCreator extends ChildBroadcastRequestCreatable<Request>> implements HashDisposable<ChildBroadcastReader<Request, RequestCreator>>
{

	@Override
	public void dispose(ChildBroadcastReader<Request, RequestCreator> t)
	{
		t.dispose();
	}

}
