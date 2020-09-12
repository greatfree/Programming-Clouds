package org.greatfree.message.multicast.container;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/02/2018, Bing Li
public class ChildResponse extends ServerMessage
{
	private static final long serialVersionUID = -5004187798822759536L;
	
	private MulticastResponse response;

	// It is not reasonable to notify the root when the child responds requests from the root since the notifications from the root also imposes workloads to the child. So I think the states of workloads should be implemented only on the application level. 09/11/2020, Bing Li
	// The below boolean values indicate whether the child is busy, idle or normal. The root is able to determine its behavior within the cluster. 09/11/2020, Bing Li
	/*
	private boolean isBusy;
	private boolean isIdle;
	private boolean isNormal;
	*/

	/*
	 * The constructor is used when the child of the cluster is normal in terms of the pressure of workload. 09/11/2020, Bing Li
	 */
	public ChildResponse(MulticastResponse response)
	{
		super(ClusterMessageType.CHILD_RESPONSE);
		this.response = response;
//		this.isBusy = false;
//		this.isIdle = false;
//		this.isNormal = true;
	}

	/*
	 * 
	 * It is not reasonable to notify the root when the child responds requests from the root since the notifications from the root also imposes workloads to the child. So I think the states of workloads should be implemented only on the application level. 09/11/2020, Bing Li
	 * 
	 * When the child busy/idle state needs to be indicated, the constructor is employed. 09/11/2020, Bing Li
	 */
	/*
	public ChildResponse(MulticastResponse response, boolean isBusy, boolean isIdle)
	{
		super(ClusterMessageType.CHILD_RESPONSE);
		this.response = response;
		this.isBusy = isBusy;
		this.isIdle = isIdle;
		this.isNormal = false;
	}
	*/

	public MulticastResponse getResponse()
	{
		return this.response;
	}

	/*
	public boolean isNormal()
	{
		return this.isNormal;
	}
	
	public boolean isBusy()
	{
		return this.isBusy;
	}
	
	public boolean isIdle()
	{
		return this.isIdle;
	}
	*/
}
