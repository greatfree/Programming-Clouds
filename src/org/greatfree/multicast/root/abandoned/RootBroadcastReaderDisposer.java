package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.reuse.HashDisposable;

/*
 * This is a disposer that collects the resources of the broadcast reader, MainBroadcastReader. It is used by the resource pool for MainBroadcastReader. 05/05/2017, Bing Li
 */

// Created: 05/05/2017, Bing Li
class RootBroadcastReaderDisposer<Data extends Serializable, Request extends OldMulticastRequest, Response extends MulticastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>> implements HashDisposable<RootBroadcastReader<Data, Request, Response, RequestCreator>>
{

	@Override
	public void dispose(RootBroadcastReader<Data, Request, Response, RequestCreator> t)
	{
		t.dispose();
	}

}
