package org.greatfree.multicast.child;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.util.Builder;

// Created: 08/24/2018, Bing Li
class ChildCSClient<Notification extends MulticastNotification, Request extends MulticastRequest>
{
	private ChildMulticastEventer<Notification> eventer;
	private ChildMulticastReader<Request> reader;

	public ChildCSClient(ChildCSClientBuilder<Notification, Request> builder)
	{
		this.eventer = new ChildMulticastEventer<Notification>(builder.getLocalIPKey(), new SyncRemoteEventer<Notification>(builder.getClientPool()), builder.getTreeBranchCount());
		this.reader = new ChildMulticastReader<Request>(builder.getLocalIPKey(), new SyncRemoteEventer<Request>(builder.getClientPool()), builder.getTreeBranchCount());
	}
	
	public void dispose() throws IOException, ClassNotFoundException
	{
		this.eventer.dispose();
		this.reader.dispose();
	}
	
	public static class ChildCSClientBuilder<Notification extends MulticastNotification, Request extends MulticastRequest> implements Builder<ChildCSClient<Notification, Request>>
	{
		private String localIPKey;
		private FreeClientPool clientPool;
		private int treeBranchCount;
		
		public ChildCSClientBuilder()
		{
		}

		public ChildCSClientBuilder<Notification, Request> localIPKey(String localIPKey)
		{
			this.localIPKey = localIPKey;
			return this;
		}

		public ChildCSClientBuilder<Notification, Request> clientPool(FreeClientPool clientPool)
		{
			this.clientPool = clientPool;
			return this;
		}
		
		public ChildCSClientBuilder<Notification, Request> treeBranchCount(int treeBranchCount)
		{
			this.treeBranchCount = treeBranchCount;
			return this;
		}

		@Override
		public ChildCSClient<Notification, Request> build() throws IOException
		{
			return new ChildCSClient<Notification, Request>(this);
		}
		
		public String getLocalIPKey()
		{
			return this.localIPKey;
		}

		public FreeClientPool getClientPool()
		{
			return this.clientPool;
		}
		
		public int getTreeBranchCount()
		{
			return this.treeBranchCount;
		}
	}

	public void notify(Notification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.eventer.disseminate(notification);
	}

	public void read(Request request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.reader.disseminate(request);
	}
}
