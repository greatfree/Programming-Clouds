package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.reuse.HashDisposable;

/*
 * This is a disposer that collects the resources of the unicast reader, UnicastRequestReader. It is used by the resource pool for UnicastRequestReader. 05/07/2017, Bing Li
 */

// Created: 05/07/2017, Bing Li
class RootUnicastReaderDisposer<Data extends Serializable, Request extends OldMulticastRequest, Response extends MulticastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>> implements HashDisposable<RootUnicastReader<Data, Request, Response, RequestCreator>>
{

	@Override
	public void dispose(RootUnicastReader<Data, Request, Response, RequestCreator> t)
	{
		t.dispose();
	}

}
