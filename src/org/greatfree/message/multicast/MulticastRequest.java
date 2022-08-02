package org.greatfree.message.multicast;

import org.greatfree.util.Tools;

/*
 * The message is a broadcast request to be sent through all of the distributed nodes to retrieve required data. For multicasting is required, it extends MulticastMessage. 11/28/2014, Bing Li
 */

// Created: 05/13/2017, Bing Li
public abstract class MulticastRequest extends MulticastNotification
{
	private static final long serialVersionUID = 3004660627027017743L;

	// To implement the broadcast, the request contains the collaborator key that is associated with the instance of Collaborator that is waiting at the source node that sends the broadcast request. Once if the required data is obtained at one node, it must be returned to the source from the destination. The collaborator must be signaled by the responses. As a broadcast, all of the nodes must respond before the requesting is finished. 11/28/2014, Bing Li
	private String collaboratorKey;
	
	// I decide to remove the line. Instead, each child needs to request from the registry to get the root IP when it is started to broadcast as requests. 05/20/2017, Bing Li
	// The IP address of the root of the cluster. One distributed node needs to respond to the root through the address. 05/20/2017, Bing Li
//	private IPAddress rootAddress;

	/*
	 * Initialize. This constructor is used for the case when the requestor has no children nodes. 11/28/2014, Bing Li
	 */

//	public ClusterBroadcastRequest(int type, String key, String collaboratorKey, IPAddress rootAddress)
//	public ClusterBroadcastRequest(int type, String key, String collaboratorKey)
//	public ClusterBroadcastRequest(int type, String collaboratorKey)
//	public MulticastRequest(int applicationID)
	public MulticastRequest(int type)
	{
		super(type);
		this.collaboratorKey = Tools.generateUniqueKey();
//		this.rootAddress = rootAddress;
	}

	/*
	 * Initialize. The parameters are explained as follows. 11/28/2014, Bing Li
	 * 
	 * 		int dataType: the message type;
	 * 
	 * 		String key: the message key;
	 * 
	 * 		String collaboratorKey: the collaborator key pointing to the instance of Collaborator to collect all of the results by synchronization
	 * 
	 * 		HashMap<String, IPAddress> childrenServerMap: when an intermediate node receives the request, it must forward the request to its children with the assistance of information.	
	 */

//	public ClusterBroadcastRequest(int dataType, String key, String collaboratorKey, IPAddress rootAddress, HashMap<String, IPAddress> childrenServerMap)
//	public ClusterBroadcastRequest(int dataType, String key, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap)
//	public ClusterBroadcastRequest(int dataType, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap)
	/*
	public MulticastRequest(int dataType, Map<String, IPAddress> childrenServerMap)
	{
//		super(dataType, key, childrenServerMap);
		super(dataType, childrenServerMap);
		this.collaboratorKey = Tools.generateUniqueKey();
//		this.rootAddress = rootAddress;
	}
	*/

	/*
	 * Expose the collaborator. 11/28/2014, Bing Li
	 */
	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}

	/*
	 * Expose the IP address. 05/20/2017, Bing Li
	 */
	/*
	public IPAddress getIPAddress()
	{
		return this.rootAddress;
	}
	*/
}
