package org.greatfree.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.PeerAddressStream;

// Created: 09/11/2018, Bing Li
class PeerAddressRequestThreadCreator implements RequestQueueCreator<PeerAddressRequest, PeerAddressStream, PeerAddressResponse, PeerAddressRequestThread>
{

	@Override
	public PeerAddressRequestThread createInstance(int taskSize)
	{
		return new PeerAddressRequestThread(taskSize);
	}

}
