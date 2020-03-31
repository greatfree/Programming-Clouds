package ca.multicast.search.root;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import ca.multicast.search.message.ClientSearchQueryRequest;
import ca.multicast.search.message.ClientSearchQueryResponse;
import ca.multicast.search.message.ClientSearchQueryStream;

// Created: 03/15/2020, Bing Li
class SearchQueryRequestThreadCreator implements RequestThreadCreatable<ClientSearchQueryRequest, ClientSearchQueryStream, ClientSearchQueryResponse, SearchQueryRequestThread>
{

	@Override
	public SearchQueryRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SearchQueryRequestThread(taskSize);
	}

}
