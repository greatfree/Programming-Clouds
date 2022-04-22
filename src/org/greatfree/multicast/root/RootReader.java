package org.greatfree.multicast.root;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 08/26/2018, Bing Li
//public class RootReader<Response extends ClusterBroadcastResponse>
final class RootReader
{
	/*
	// The creator to generate broadcast requests. 11/28/2014, Bing Li
	// All of the received responses from each of the node which is retrieved. 11/28/2014, Bing Li
	private Map<String, List<MulticastResponse>> responses;
	// The collaborator that synchronizes to collect the results and determine whether the request is responded sufficiently. 11/28/2014, Bing Li
//	private Sync collaborator;
	private Map<String, Sync> syncs;
	// The time to wait for responses. If it lasts too long, it might get problems for the request processing. 11/28/2014, Bing Li
	private long waitTime;
	// The count of the total nodes in the broadcast. 11/28/2014, Bing Li
//	private AtomicInteger nodeCount;
	private Map<String, Integer> receiverCounts;
	*/
	private RootSyncMulticastor multicastor;

	private RootAsyncMulticastReader asyncReader;

	private RootRendezvousPoint rp;

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
//	public RootReader(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, long waitTime, ThreadPool pool)
	public RootReader(RootSyncMulticastor multicastor, long waitTime, ThreadPool pool)
	{
		/*
		this.responses = new ConcurrentHashMap<String, List<MulticastResponse>>();
//		this.collaborator = new Sync();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.waitTime = waitTime;
//		this.nodeCount = new AtomicInteger(0);
		this.receiverCounts = new ConcurrentHashMap<String, Integer>();
		*/
		this.asyncReader = new RootAsyncMulticastReader(multicastor, pool);

//		this.rp = new RootRendezvousPoint(pool, waitTime);
		this.rp = new RootRendezvousPoint(waitTime);
//		Multicastor.SEND().init(clientPool, rootBranchCount, treeBranchCount, this.rp);
		this.multicastor = multicastor;
		this.multicastor.setRP(this.rp);
	}

	/*
	 * Dispose the broadcast requestor. 11/28/2014, Bing Li
	 */
	public void dispose() throws IOException, InterruptedException
	{
		this.asyncReader.dispose();
		/*
		// Signal all of the current threads waiting for responses
		for (Sync entry : this.syncs.values())
		{
			entry.signalAll();
		}
		// Clear the collections
		this.syncs.clear();
		this.responses.clear();
		this.receiverCounts.clear();
		*/
		this.rp.dispose();
//		Multicastor.SEND().dispose();
	}

	/*
	 * Save one particular response from the remote node. 11/28/2014, Bing Li
	 */
	/*
	public void saveResponse(MulticastResponse response)
	{
		// Check whether the response corresponds to the requestor. 11/29/2014, Bing Li
		if (!this.responses.containsKey(response.getCollaboratorKey()))
		{
			this.responses.put(response.getCollaboratorKey(), new ArrayList<MulticastResponse>());
		}
		this.responses.get(response.getCollaboratorKey()).add(response);
		if (this.responses.get(response.getCollaboratorKey()).size() >= this.receiverCounts.get(response.getCollaboratorKey()))
		{
			if (this.syncs.containsKey(response.getCollaboratorKey()))
			{
				this.syncs.get(response.getCollaboratorKey()).signal();
			}
		}
	}
	*/
	
	public RootRendezvousPoint getRP()
	{
		return this.rp;
	}
	
	/*
	 * The failed node should be removed from the waiting counts. 08/26/2018, Bing Li
	 */
	/*
	private void decrementNode(String collaboratorKey)
	{
		int count = this.receiverCounts.get(collaboratorKey);
		this.receiverCounts.put(collaboratorKey, --count);
	}
	*/

	/*
	 * Waiting for responses from the distributed nodes. 08/26/2018, Bing Li
	 */
	/*
	private List<MulticastResponse> waitForResponses(String collaboratorKey)
	{
		// Create an instance of Sync, a wait/notify mechanism, for one multicasting request
		this.syncs.put(collaboratorKey, new Sync(false));
		// Wait for responses from distributed nodes
		this.syncs.get(collaboratorKey).holdOn(this.waitTime);
		// Remove the instance of Sync for the multicasting request
		this.syncs.remove(collaboratorKey);
		// Get the list that keeps the responses from distributed nodes
		List<MulticastResponse> results = this.responses.get(collaboratorKey);
		// Remove the list from the map
		this.responses.remove(collaboratorKey);
		// Return the list of responses
		return results;
	}
	*/

	/*
	 * For reading, only the task to send requests is processed asynchronously. It has to wait for responses from children. 09/15/2018, Bing Li
	 */
	public List<MulticastResponse> asyncRead(MulticastRequest request)
	{
		this.asyncReader.asyncRead(request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Broadcast the request in a synchronous way. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		this.multicastor.read(request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncReadWithNResponses(MulticastRequest request, int n)
	{
		this.asyncReader.asyncRead(new SizeMulticastRequest(request, n));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Anycast the request synchronously to the certain number of children. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncReadWithNResponses(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.readWithNResponses(request, n);
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
//		return this.waitForResponses(request.getCollaboratorKey());
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncRead(Set<String> childrenKeys, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new ChildrenMulticastRequest(request, childrenKeys));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Broadcast the request synchronously to the specified children. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncRead(Set<String> childrenKeys, MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		this.multicastor.read(childrenKeys, request);
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
//		return this.waitForResponses(request.getCollaboratorKey());
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Broadcasting a request synchronously within randomly selected N children. 09/11/2020, Bing Li
	 */
	public List<MulticastResponse> syncReadWithinNChildren(int n, MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		this.multicastor.readWithinNChildren(request, n);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * Broadcasting a request asynchronously within randomly selected N children. 09/11/2020, Bing Li
	 */
	public List<MulticastResponse> asyncReadWithinNChildren(int n, MulticastRequest request)
	{
		this.asyncReader.asynRead(new RandomChildrenMulticastRequest(request, n));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Broadcasting a request synchronously within one partition which contains the specified N children. 09/11/2020, Bing Li
	 */
	public MulticastResponse syncReadUponPartition(Set<String> childrenKeys, MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		this.multicastor.read(childrenKeys, request);
		return this.rp.waitForResponseUponPartition(request.getCollaboratorKey());
	}

	/*
	 * Broadcasting a request asynchronously within one partition which contains the specified N children. 09/11/2020, Bing Li
	 */
	public MulticastResponse asyncReadUponPartition(Set<String> childrenKeys, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new ChildrenMulticastRequest(request, childrenKeys));
		return this.rp.waitForResponseUponPartition(request.getCollaboratorKey());
	}

	public List<MulticastResponse> asyncRead(Set<String> childrenKeys, MulticastRequest request, int n)
	{
		this.asyncReader.asyncRead(new ChildrenSizeMulticastRequest(request, childrenKeys, n));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Anycast the request synchronously to the specified children and only expect a certain number of responses from the children. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncRead(Set<String> childrenKeys, MulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.read(childrenKeys, request, n);
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
//		return this.waitForResponses(request.getCollaboratorKey());
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncNearestRead(Set<String> dataKeys, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new NearestKeysMulticastRequest(request, dataKeys));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * Broadcast the request to nearest children synchronously in terms of the similarity to the specified data keys respectively. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncNearestRead(Set<String> dataKeys, MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		/*
		Set<String> nearestClientKeys = Sets.newHashSet();
		for (String dataKey : dataKeys)
		{
			nearestClientKeys.add(Tools.getClosestKey(dataKey, this.reader.getClientKeys()));
		}
		return this.syncRead(nearestClientKeys, request);
		*/
		this.multicastor.nearestRead(dataKeys, request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncRead(String childKey, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new ChildKeyMulticastRequest(request, childKey));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * Unicast the request synchronously to one specified client. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncRead(String childKey, MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.read(childKey, request);
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
//		return this.waitForResponses(request.getCollaboratorKey());
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncRandomRead(MulticastRequest request)
	{
		this.asyncReader.asyncRandomRead(request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * Unicast the request synchronously to one client that is selected randomly. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncRandomRead(MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.randomRead(request);
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
//		return this.waitForResponses(request.getCollaboratorKey());
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncNearestRead(String key, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new NearestKeyMulticastRequest(request, key));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Unicast the request synchronously to one nearest client in terms of the similarity to a specified key. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncNearestRead(String key, MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.nearestRead(key, request);
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
//		return this.waitForResponses(request.getCollaboratorKey());
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

}
