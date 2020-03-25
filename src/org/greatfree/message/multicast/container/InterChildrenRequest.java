package org.greatfree.message.multicast.container;

import org.greatfree.message.multicast.MulticastMessageType;

/*
 * Some of the code is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
 */

// Created: 02/23/2019, Bing Li
// public abstract class IntercastChildRequest extends Notification
public abstract class InterChildrenRequest extends Request
{
	private static final long serialVersionUID = -975750061521485612L;
	
//	private String destinationKey;
//	private Set<String> destinationKeys;
	
	private String subRootIP;
	private int subRootPort;
	
	private IntercastRequest ir;

//	public InterChildrenRequest(int requestType, int applicationID, Set<String> dstKeys)
//	public InterChildrenRequest(String subRootIP, int subRootPort, IntercastRequest ir)
	public InterChildrenRequest(IntercastRequest ir)
	{
		super(MulticastMessageType.INTER_CHILDREN_REQUEST, ir.getApplicationID());
//		this.destinationKey = UtilConfig.EMPTY_STRING;
//		this.destinationKeys = dstKeys;
//		this.subRootIP = subRootIP;
//		this.subRootPort = subRootPort;
		this.ir = ir;
	}
	
	public void setSubRootIP(String ip)
	{
		this.subRootIP = ip;
	}
	
	public String getSubRootIP()
	{
		return this.subRootIP;
	}
	
	public void setSubRootPort(int port)
	{
		this.subRootPort = port;
	}
	
	public int getSubRootPort()
	{
		return this.subRootPort;
	}
	
	public IntercastRequest getIntercastRequest()
	{
		return this.ir;
	}

	/*
	public InterChildrenRequest(String dstKey, int applicationID)
	{
		super(dstKey, MulticastMessageType.INTER_CHILD_UNICAST_REQUEST, applicationID);
		this.destinationKey = dstKey;
		this.destinationKeys = null;
	}
	
	public String getDestinationKey()
	{
		return this.destinationKey;
	}
	
	public Set<String> getDestinationKeys()
	{
		return this.destinationKeys;
	}
	*/
}
