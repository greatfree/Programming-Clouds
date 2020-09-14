package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.AdditionalChildrenRequest;
import org.greatfree.cluster.message.AdditionalChildrenResponse;
import org.greatfree.cluster.message.AdditionalChildrenStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 09/13/2020, Bing Li
class AdditionalChildrenRequestThreadCreator implements RequestThreadCreatable<AdditionalChildrenRequest, AdditionalChildrenStream, AdditionalChildrenResponse, AdditionalChildrenRequestThread>
{

	@Override
	public AdditionalChildrenRequestThread createRequestThreadInstance(int taskSize)
	{
		return new AdditionalChildrenRequestThread(taskSize);
	}

}
