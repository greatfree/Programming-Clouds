package ca.multicast.search.root;

import java.io.IOException;
import java.util.List;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.multicast.root.RootClient;
import org.greatfree.multicast.root.RootRendezvousPoint;

import ca.multicast.search.message.SearchQueryResponse;

// Created: 03/14/2020, Bing Li
class SearchMulticastor
{
	private RootClient client;

	private SearchMulticastor()
	{
	}
	
	private static SearchMulticastor instance = new SearchMulticastor();
	
	public static SearchMulticastor ROOT()
	{
		if (instance == null)
		{
			instance = new SearchMulticastor();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop() throws IOException, InterruptedException
	{
		this.client.close();
	}
	
	public void start(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, long waitTime, ThreadPool pool)
	{
		this.client = new RootClient(clientPool, rootBranchCount, treeBranchCount, waitTime, pool);
	}
	
	public RootRendezvousPoint getRP()
	{
		return this.client.getRP();
	}
	
	public void broadcastNotify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.broadcastNotify(notification);
	}

	public void unicastNotify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.unicastNotify(notification);
	}

	public List<SearchQueryResponse> broadRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.client.broadcastRead(request, SearchQueryResponse.class);
	}
}
