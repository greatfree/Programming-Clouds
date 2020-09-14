package org.greatfree.message.multicast.container;

import java.util.Set;

import org.greatfree.cluster.ClusterConfig;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/23/2018, Bing Li
public abstract class Request extends MulticastRequest
{
	private static final long serialVersionUID = 8212022646043424214L;
	
	private String clientKey;
	private int requestType;
	private int applicationID;
	private int partitionIndex;
	private Set<String> childrenKeys;

	/*
	 * 
	 * This key is important. Developers can set the value. So they can decide how to balance the load. For example, in the case of S3, all of the encoded data slices for the same encoding block can be sent to a unique child for merging. The client key can be the ID of the encoding block. 07/11/2020, Bing Li
	 * 
	 * The constructor is usually used for the nearest unicasting. So the client key is required for nearest measure. 10/28/2018, Bing Li 
	 */
//	public Request(String clientKey, int requestType, int applicationID)
	public Request(String clientKey, int applicationID)
	{
		super(MulticastMessageType.REQUEST);
		this.clientKey = clientKey;
//		this.requestType = requestType;
		this.requestType = MulticastMessageType.UNICAST_REQUEST;
		this.applicationID = applicationID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = null;
	}

	/*
	 * The below two constructors are used together for the root and the children, respectively. 10/28/2018, Bing Li
	 */

	/*
	 * This constructor is usually used for normal broadcasting. 10/28/2018, Bing Li
	 */
	public Request(int requestType, int applicationID)
	{
		super(MulticastMessageType.REQUEST);
		this.requestType = requestType;
		this.applicationID = applicationID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = null;
	}

	/*
	 * This constructor is usually used for broadcasting upon replication. The partition index is required to be specified. 10/28/2018, Bing Li
	 */
	public Request(int requestType, int applicationID, int partitionIndex)
	{
		super(MulticastMessageType.REQUEST);
		this.requestType = requestType;
		this.applicationID = applicationID;
		this.partitionIndex = partitionIndex;
		this.childrenKeys = null;
	}

	/*
	 * When broadcasting is performed within specified children, the constructor is employed. 09/13/2020, Bing Li
	 */
	public Request(int requestType, int applicationID, Set<String> childrenKeys)
	{
		super(MulticastMessageType.REQUEST);
		this.requestType = requestType;
		this.applicationID = applicationID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = childrenKeys;
	}

	/*
	 * This constructor is usually used for normal broadcasting. 10/28/2018, Bing Li
	 */
	/*
	public Request(int requestType, int applicationID, Map<String, IPAddress> childrenIPs)
	{
		super(ClusterMessageType.REQUEST, childrenIPs);
		this.requestType = requestType;
		this.applicationID = applicationID;
	}
	*/
	
	/*
	 * The below two constructors are used together for the root and the children, respectively. 10/28/2018, Bing Li
	 */

	/*
	 * The below design results in a cumbersome class. So such system level requests are available until now. So the below design should be removed. 10/28/2018, Bing Li
	 * 
	 * For some system messages, it is unnecessary to assign the application ID. 10/28/2018, Bing Li
	 */
	/*
	public Request(int requestType)
	{
		super(ClusterMessageType.REQUEST);
		this.requestType = requestType;
	}
	*/

	/*
	 * For some system messages, it is unnecessary to assign the application ID. 10/28/2018, Bing Li
	 */
	/*
	public Request(int requestType, Map<String, IPAddress> childrenIPs)
	{
		super(ClusterMessageType.REQUEST, childrenIPs);
		this.requestType = requestType;
	}
	*/
	
	public String getClientKey()
	{
		return this.clientKey;
	}

	public int getRequestType()
	{
		return this.requestType;
	}
	
	public int getApplicationID()
	{
		return this.applicationID;
	}
	
	public int getPartitionIndex()
	{
		return this.partitionIndex;
	}
	
	public Set<String> getChildrenKeys()
	{
		return this.childrenKeys;
	}
}
