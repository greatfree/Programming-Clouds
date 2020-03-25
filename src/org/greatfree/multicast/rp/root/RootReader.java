package org.greatfree.multicast.rp.root;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.multicast.RendezvousPoint;

// Created: 10/15/2018, Bing Li
class RootReader
{
	private RootSyncMulticastor multicastor;

	private RootAsyncMulticastReader asyncReader;
	
//	private RendezvousPoint rp;
	private RendezvousPoint rp;
	
	public RootReader(RootSyncMulticastor multicastor, long waitTime, ThreadPool pool)
	{
		this.asyncReader = new RootAsyncMulticastReader(multicastor, pool);
//		this.rp = new RendezvousPoint(waitTime);
		this.rp = new RendezvousPoint(waitTime);
		this.multicastor = multicastor;
		this.multicastor.setRP(rp);
	}
	
	public void dispose() throws InterruptedException
	{
		this.asyncReader.dispose();
		this.rp.dispose();
	}
	
//	public RendezvousPoint getRP()
	public RendezvousPoint getRP()
	{
		return this.rp;
	}
	
	public List<MulticastResponse> asyncRead(RPMulticastRequest request)
	{
		this.asyncReader.asyncRead(request);
		return this.rp.waitForResponses(request.getCollaboratorKey()).getResponses();
	}

	public List<MulticastResponse> syncRead(RPMulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		this.multicastor.read(request);
		return this.rp.waitForResponses(request.getCollaboratorKey()).getResponses();
	}
	
	public List<MulticastResponse> asyncRead(RPMulticastRequest request, int n)
	{
		this.asyncReader.asyncRead(new SizeMulticastRequest(request, n));
		return this.rp.waitForResponses(request.getCollaboratorKey()).getResponses();
	}
	
	public List<MulticastResponse> syncRead(RPMulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.read(request, n);
		return this.rp.waitForResponses(request.getCollaboratorKey()).getResponses();
	}
	
	public List<MulticastResponse> asyncRead(Set<String> childrenKeys, RPMulticastRequest request)
	{
		this.asyncReader.asyncRead(new ChildrenMulticastRequest(request, childrenKeys));
		return this.rp.waitForResponses(request.getCollaboratorKey()).getResponses();
	}
	
	public List<MulticastResponse> syncRandomRead(RPMulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.randomRead(request);
		return this.rp.waitForResponses(request.getCollaboratorKey()).getResponses();
	}
	
	public List<MulticastResponse> asyncRandomRead(RPMulticastRequest request)
	{
		this.asyncReader.asyncRandomRead(request);
		return this.rp.waitForResponses(request.getCollaboratorKey()).getResponses();
	}
	
	public List<MulticastResponse> syncNearestRead(String key, RPMulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.nearestRead(key, request);
		return this.rp.waitForResponses(request.getCollaboratorKey()).getResponses();
	}

	public List<MulticastResponse> asyncNearestRead(String key, RPMulticastRequest request)
	{
		this.asyncReader.asyncRead(new NearestKeyMulticastRequest(request, key));
		return this.rp.waitForResponses(request.getCollaboratorKey()).getResponses();
	}
}
