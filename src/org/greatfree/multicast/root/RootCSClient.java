package org.greatfree.multicast.root;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.util.Builder;

/*
 * The class needs to put notifications/requests together. Both of them are not associated in most time. 08/26/2018, Bing Li
 */

// Created: 08/22/2018, Bing Li
class RootCSClient<Notification extends MulticastMessage, Request extends MulticastRequest, Response extends MulticastResponse>
{
	private RootMulticastEventer<Notification> eventer;
	private RootMulticastReader<Request, Response> reader;

	// The TCP client pool that sends notifications to the chatting server. 05/27/2017, Bing Li
//	private FreeClientPool clientPool;
	
	public RootCSClient(RootCSClientBuilder<Notification, Request, Response> builder)
	{
//		this.clientPool = builder.getClientPool();
//		this.clientPool.setIdleChecker(builder.getClientIdleCheckDelay(), builder.getClientIdleCheckPeriod(), builder.getClientMaxIdleTime());

		this.eventer = new RootMulticastEventer<Notification>(new SyncRemoteEventer<Notification>(builder.getClientPool()), builder.getRootBranchCount(), builder.getTreeBranchCount());
		this.reader = new RootMulticastReader<Request, Response>(new SyncRemoteEventer<Request>(builder.getClientPool()), builder.getRootBranchCount(), builder.getTreeBranchCount(), builder.getWaitTime());
	}
	
	public void dispose() throws IOException
	{
//		this.clientPool.dispose();
		this.eventer.dispose();
		this.reader.dispose();
	}
	
	public static class RootCSClientBuilder<Notification extends MulticastMessage, Request extends MulticastRequest, Response extends MulticastResponse> implements Builder<RootCSClient<Notification, Request, Response>>
	{
		/*
		private int clientPoolSize;
		private long clientIdleCheckDelay;
		private long clientIdleCheckPeriod;
		private long clientMaxIdleTime;
		*/
		private FreeClientPool clientPool;
		private int rootBranchCount;
		private int treeBranchCount;
		private long waitTime;
		
		public RootCSClientBuilder()
		{
		}

		/*
		public RootCSClientBuilder clientPoolSize(int clientPoolSize)
		{
			this.clientPoolSize = clientPoolSize;
			return this;
		}
		
		public RootCSClientBuilder clientIdleCheckDelay(long clientIdleCheckDelay)
		{
			this.clientIdleCheckDelay = clientIdleCheckDelay;
			return this;
		}
		
		public RootCSClientBuilder clientIdleCheckPeriod(long clientIdleCheckPeriod)
		{
			this.clientIdleCheckPeriod = clientIdleCheckPeriod;
			return this;
		}
		
		public RootCSClientBuilder clientMaxIdleTime(int clientMaxIdleTime)
		{
			this.clientMaxIdleTime = clientMaxIdleTime;
			return this;
		}
		*/
		
		public RootCSClientBuilder<Notification, Request, Response> clientPool(FreeClientPool clientPool)
		{
			this.clientPool = clientPool;
			return this;
		}
		
		public RootCSClientBuilder<Notification, Request, Response> rootBranchCount(int rootBranchCount)
		{
			this.rootBranchCount = rootBranchCount;
			return this;
		}
		
		public RootCSClientBuilder<Notification, Request, Response> treeBranchCount(int treeBranchCount)
		{
			this.treeBranchCount = treeBranchCount;
			return this;
		}
		
		public RootCSClientBuilder<Notification, Request, Response> waitTime(long waitTime)
		{
			this.waitTime = waitTime;
			return this;
		}

		@Override
		public RootCSClient<Notification, Request, Response> build() throws IOException
		{
			return new RootCSClient<Notification, Request, Response>(this);
		}

		/*
		public int getClientPoolSize()
		{
			return this.clientPoolSize;
		}
		
		public long getClientIdleCheckDelay()
		{
			return this.clientIdleCheckDelay;
		}
		
		public long getClientIdleCheckPeriod()
		{
			return this.clientIdleCheckPeriod;
		}
		
		public long getClientMaxIdleTime()
		{
			return this.clientMaxIdleTime;
		}
		*/
		
		public FreeClientPool getClientPool()
		{
			return this.clientPool;
		}
		
		public int getRootBranchCount()
		{
			return this.rootBranchCount;
		}
		
		public int getTreeBranchCount()
		{
			return this.treeBranchCount;
		}
		
		public long getWaitTime()
		{
			return this.waitTime;
		}
	}

	public void notify(Notification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.eventer.disseminate(notification);
	}
	
	public void notify(Notification notification, Set<String> nodeKeys) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.eventer.disseminate(notification, nodeKeys);
	}
	
	public void notify(Notification notification, String nodeKey) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.eventer.disseminate(notification, nodeKey);
	}
	
	public void notifyNearest(String key, Notification notification) throws IOException
	{
		this.eventer.nearestDisseminate(key, notification);
	}
	
	public void saveResponse(Response response)
	{
		this.reader.saveResponse(response);
	}
	
	public List<Response> read(Request request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.reader.broadcast(request);
	}
	
	public List<Response> readNearest(Set<String> keys, Request request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.reader.broadcastNearestly(keys, request);
	}

	public List<Response> anycastRead(Request request, int n) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.reader.anycast(request, n);
	}

	public List<Response> read(Set<String> clientKeys, Request request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.reader.broadcast(clientKeys, request);
	}
	
	public List<Response> anycastRead(Set<String> clientKeys, Request request, int n) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.reader.anycast(clientKeys, request, n);
	}
	
	public List<Response> unicastRead(String clientKey, Request request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.reader.unicast(clientKey, request);
	}
	
	public List<Response> unicastRead(Request request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.reader.unicast(request);
	}
	
	public List<Response> readNearest(String key, Request request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.reader.unicastNearestly(key, request);
	}
}
