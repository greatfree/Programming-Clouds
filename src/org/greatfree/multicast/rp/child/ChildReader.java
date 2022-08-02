package org.greatfree.multicast.rp.child;

import java.io.IOException;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.multicast.RendezvousPoint;
import org.greatfree.util.UtilConfig;

// Created: 10/10/2018, Bing Li
class ChildReader
{
	private ChildSyncMulticastor multicastor;
	// The actor to perform multicasting asynchronously. 09/10/2018, Bing Li
	private ChildAsyncMulticastReader asyncReader;

	private RendezvousPoint rp;

	public ChildReader(ChildSyncMulticastor multicastor, long waitTime, ThreadPool pool)
	{
		this.multicastor = multicastor;
		this.asyncReader = new ChildAsyncMulticastReader(multicastor, pool);
		this.rp = new RendezvousPoint(waitTime);
		this.multicastor.setRP(this.rp);
	}
	
	public void dispose() throws IOException, InterruptedException
	{
		this.asyncReader.dispose();
		this.rp.dispose();
	}
	
//	public OldRendezvousPoint getRP()
	public RendezvousPoint getRP()
	{
		return this.rp;
	}
	
	public void asyncRead(RPMulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		// Since the asynchronous operation of reading, the receiver size needs to be set before reading is performed asynchronously. 12/17/2018, Bing Li
		// The size of children IPs is not just its immediate children. The size should be the count of all of the nodes in the subtree, whose root is the current local node. 10/13/2018, Bing Li
		// The RP needs to wait for a couple of responses from its children. The size is the total of its immediate children and the one of its own. 10/10/2018, Bing Li
		if (request.getChildrenIPs() != UtilConfig.NO_IPS)
		{
			this.rp.setReceiverSize(request.getCollaboratorKey(), request.getChildrenIPs().size() + 1);
		}
		this.asyncReader.read(request);
//		System.out.println("1) ChildReader-asyncRead(): RP-IP = " + ChildPeer.CHILD().getParentAddress());
		System.out.println("1.5) ChildReader-asyncRead(): RP Receiver Size = " + this.rp.getReceiverSize(request.getCollaboratorKey()));
		this.multicastor.notify(request.getRPAddress().getIP(), request.getRPAddress().getPort(), this.rp.waitForResponses(request.getCollaboratorKey()));
		
		/*
		 * Today I found the below problem, The RP multicasting is not used and tested sufficiently. When writing the book, I think the below line should be a bug. The current API is a system-level one. However, it employs the application-level code. The above line should be correct. 07/16/2022, Bing Li 
		 * 
		 */
//		this.multicastor.notify(ChildPeer.CHILD().getParentAddress().getIP(), ChildPeer.CHILD().getParentAddress().getPort(), this.rp.waitForResponses(request.getCollaboratorKey()));
//		System.out.println("2) ChildReader-asyncRead(): RP-IP = " + ChildPeer.CHILD().getParentAddress());
	}
	
	/*
	 * Disseminate the instance of Message. The message here is the one which is just received. It must be forwarded by the local client. 11/11/2014, Bing Li
	 */
	public void syncRead(RPMulticastRequest request) throws IOException, DistributedNodeFailedException, InterruptedException
	{
		// Since the asynchronous operation of reading, the receiver size needs to be set before reading is performed asynchronously. 12/17/2018, Bing Li
		// The size of children IPs is not just its immediate children. The size should be the count of all of the nodes in the subtree, whose root is the current local node. 10/13/2018, Bing Li
		// The RP needs to wait for a couple of responses from its children. The size is the total of its immediate children and the one of its own. 10/10/2018, Bing Li 
		if (request.getChildrenIPs() != UtilConfig.NO_IPS)
		{
			this.rp.setReceiverSize(request.getCollaboratorKey(), request.getChildrenIPs().size() + 1);
		}
		this.multicastor.read(request);
//		System.out.println("ChildReader-syncRead(): RP-IP = " + ChildPeer.CHILD().getParentAddress());
		this.multicastor.notify(request.getRPAddress().getIP(), request.getRPAddress().getPort(), this.rp.waitForResponses(request.getCollaboratorKey()));

		/*
		 * Today I found the below problem, The RP multicasting is not used and tested sufficiently. When writing the book, I think the below line should be a bug. The current API is a system-level one. However, it employs the application-level code. The above line should be correct. 07/16/2022, Bing Li 
		 * 
		 */
//		this.multicastor.notify(ChildPeer.CHILD().getParentAddress().getIP(), ChildPeer.CHILD().getParentAddress().getPort(), this.rp.waitForResponses(request.getCollaboratorKey()));
	}
}
